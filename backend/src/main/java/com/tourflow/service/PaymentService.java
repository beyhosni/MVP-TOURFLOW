package com.tourflow.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.tourflow.model.Booking;
import com.tourflow.model.Payment;
import com.tourflow.model.PaymentStatus;
import com.tourflow.repository.BookingRepository;
import com.tourflow.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public String createStripeCheckoutSession(UUID bookingId, String successUrl, String cancelUrl) {
        // Récupérer la réservation
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + bookingId));

        // Vérifier que la réservation est en attente
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Impossible de créer une session de paiement pour une réservation qui n'est pas en attente");
        }

        // Créer ou mettre à jour le paiement
        Payment payment = booking.getPayment();
        if (payment == null) {
            payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(booking.getTotalPrice());
            payment.setStatus(PaymentStatus.PENDING);
        }

        // Créer la session Stripe Checkout
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(BigDecimal.valueOf(booking.getTotalPrice() * 100).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(booking.getTour().getTitle())
                                                                .setDescription("Réservation pour " + booking.getParticipants() + " participant(s)")
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .setMetadata(Map.of(
                        "bookingId", bookingId.toString()
                ))
                .setExpiresAt(Date.from(booking.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant()).getTime() / 1000)
                .build();

        try {
            Session session = Session.create(params);

            // Mettre à jour le paiement avec les IDs Stripe
            payment.setStripeSessionId(session.getId());
            payment.setPaymentIntentId(session.getPaymentIntent());

            // Sauvegarder le paiement
            payment = paymentRepository.save(payment);

            // Lier le paiement à la réservation
            booking.setPayment(payment);
            bookingRepository.save(booking);

            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException("Erreur lors de la création de la session de paiement Stripe", e);
        }
    }

    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {
            // Vérifier la signature du webhook
            com.stripe.model.Event event = com.stripe.model.Webhook.constructEvent(
                    payload, sigHeader, stripeWebhookSecret
            );

            // Traiter l'événement en fonction de son type
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                default:
                    System.out.println("Événement non traité: " + event.getType());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement du webhook Stripe", e);
        }
    }

    @Transactional
    private void handlePaymentSucceeded(com.stripe.model.Event event) {
        com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);

        if (paymentIntent == null) {
            return;
        }

        // Récupérer le paiement via le paymentIntentId
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElse(null);

        if (payment != null) {
            // Mettre à jour le statut du paiement
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Confirmer la réservation
            bookingService.confirmBooking(payment.getBooking().getId());
        }
    }

    @Transactional
    private void handlePaymentFailed(com.stripe.model.Event event) {
        com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);

        if (paymentIntent == null) {
            return;
        }

        // Récupérer le paiement via le paymentIntentId
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElse(null);

        if (payment != null) {
            // Mettre à jour le statut du paiement
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailedAt(LocalDateTime.now());
            payment.setFailureReason(paymentIntent.getLastPaymentError() != null ? 
                    paymentIntent.getLastPaymentError().getMessage() : "Erreur de paiement inconnue");
            paymentRepository.save(payment);

            // Annuler la réservation
            bookingService.cancelBooking(payment.getBooking().getId(), "Échec du paiement");
        }
    }

    @Transactional
    public Payment refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Impossible de rembourser un paiement qui n'est pas complété");
        }

        try {
            // Créer le remboursement Stripe
            com.stripe.model.Refund refund = com.stripe.model.Refund.create(
                    com.stripe.param.RefundCreateParams.builder()
                            .setPaymentIntent(payment.getPaymentIntentId())
                            .build()
            );

            // Mettre à jour le statut du paiement
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            return payment;
        } catch (StripeException e) {
            throw new RuntimeException("Erreur lors du remboursement du paiement", e);
        }
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }
}
