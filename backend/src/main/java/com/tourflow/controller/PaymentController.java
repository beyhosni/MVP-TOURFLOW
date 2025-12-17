package com.tourflow.controller;

import com.tourflow.dto.CheckoutSessionRequest;
import com.tourflow.model.Payment;
import com.tourflow.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Paiements", description = "API pour la gestion des paiements")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Créer une session de paiement Stripe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL de la session de paiement Stripe"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou réservation non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la création de la session Stripe")
    })
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        try {
            String checkoutUrl = paymentService.createStripeCheckoutSession(
                    request.getBookingId(),
                    request.getSuccessUrl(),
                    request.getCancelUrl()
            );

            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", checkoutUrl);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir la clé publique Stripe")
    @ApiResponse(responseCode = "200", description = "Clé publique Stripe")
    @GetMapping("/stripe-publishable-key")
    public ResponseEntity<?> getStripePublishableKey() {
        Map<String, String> response = new HashMap<>();
        response.put("publishableKey", paymentService.getStripePublishableKey());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Webhook pour les notifications Stripe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook traité avec succès"),
            @ApiResponse(responseCode = "400", description = "Signature invalide ou erreur de traitement")
    })
    @PostMapping("/webhook/stripe")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {
        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Rembourser un paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement remboursé avec succès"),
            @ApiResponse(responseCode = "400", description = "Impossible de rembourser ce paiement"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(
            @Parameter(description = "ID du paiement à rembourser") @PathVariable UUID paymentId) {
        try {
            Payment payment = paymentService.refundPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
