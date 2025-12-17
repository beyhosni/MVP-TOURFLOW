package com.tourflow.service;

import com.tourflow.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendBookingPendingConfirmation(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Confirmation de réservation en attente - TourFlow");

        String content = "Bonjour " + booking.getCustomerName() + ",

" +
                "Votre réservation pour le tour "" + booking.getTour().getTitle() + "" a bien été reçue.

" +
                "Détails de la réservation :
" +
                "- Date : " + booking.getStartDate() + "
" +
                "- Participants : " + booking.getParticipants() + "
" +
                "- Prix total : " + booking.getTotalPrice() + " €

" +
                "Pour finaliser votre réservation, veuillez procéder au paiement dans les 10 minutes.
" +
                "Si le paiement n'est pas effectué dans ce délai, votre réservation sera automatiquement annulée.

" +
                "Cordialement,
" +
                "L'équipe TourFlow";

        message.setText(content);
        mailSender.send(message);
    }

    public void sendBookingConfirmedConfirmation(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Confirmation de réservation - TourFlow");

        String content = "Bonjour " + booking.getCustomerName() + ",

" +
                "Votre réservation pour le tour "" + booking.getTour().getTitle() + "" a été confirmée.

" +
                "Détails de la réservation :
" +
                "- Date : " + booking.getStartDate() + "
" +
                "- Participants : " + booking.getParticipants() + "
" +
                "- Prix total : " + booking.getTotalPrice() + " €

" +
                "Nous vous remercions pour votre confiance et vous souhaitons une excellente expérience.

" +
                "Cordialement,
" +
                "L'équipe TourFlow";

        message.setText(content);
        mailSender.send(message);
    }

    public void sendBookingCancelledConfirmation(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Annulation de réservation - TourFlow");

        String content = "Bonjour " + booking.getCustomerName() + ",

" +
                "Votre réservation pour le tour "" + booking.getTour().getTitle() + "" a été annulée.

" +
                "Raison de l'annulation : " + booking.getCancellationReason() + "

" +
                "Si vous avez des questions, n'hésitez pas à nous contacter.

" +
                "Cordialement,
" +
                "L'équipe TourFlow";

        message.setText(content);
        mailSender.send(message);
    }

    public void sendBookingExpiredNotification(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Expiration de réservation - TourFlow");

        String content = "Bonjour " + booking.getCustomerName() + ",

" +
                "Votre réservation pour le tour "" + booking.getTour().getTitle() + "" a expiré.

" +
                "Le délai de paiement de 10 minutes est écoulé et votre réservation a été automatiquement annulée.

" +
                "Si vous souhaitez toujours réserver ce tour, vous pouvez en faire une nouvelle demande sur notre plateforme.

" +
                "Cordialement,
" +
                "L'équipe TourFlow";

        message.setText(content);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Réinitialisation de mot de passe - TourFlow");

        String content = "Bonjour,

" +
                "Vous avez demandé à réinitialiser votre mot de passe sur TourFlow.

" +
                "Pour continuer, veuillez cliquer sur le lien suivant :
" +
                "https://tourflow.com/reset-password?token=" + resetToken + "

" +
                "Ce lien expirera dans 24 heures.

" +
                "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.

" +
                "Cordialement,
" +
                "L'équipe TourFlow";

        message.setText(content);
        mailSender.send(message);
    }
}
