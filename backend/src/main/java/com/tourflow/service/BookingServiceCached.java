package com.tourflow.service;

import com.tourflow.model.*;
import com.tourflow.repository.BookingRepository;
import com.tourflow.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceCached {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    @Transactional
    @CacheEvict(value = {"tourBookings", "customerBookings"}, allEntries = true)
    public Booking createBooking(UUID tourId, LocalDateTime startDate, int participants,
                               String customerName, String customerEmail, String customerPhone) {
        // Récupérer le tour
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour non trouvé avec l'ID : " + tourId));

        // Calculer la date de fin
        LocalDateTime endDate = startDate.plusMinutes(tour.getDurationMinutes());

        // Vérifier la disponibilité (pas de double booking)
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                tourId, startDate, endDate,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Ce créneau n'est plus disponible");
        }

        // Calculer le prix total
        double totalPrice = tour.getPrice() * participants;

        // Créer la réservation
        Booking booking = new Booking(
                startDate, endDate, participants, totalPrice,
                customerName, customerEmail, customerPhone, tour
        );

        // Sauvegarder la réservation
        booking = bookingRepository.save(booking);

        // Envoyer un email de confirmation de réservation en attente
        emailService.sendBookingPendingConfirmation(booking);

        return booking;
    }

    @Transactional
    @CacheEvict(value = {"bookings", "tourBookings", "customerBookings"}, allEntries = true)
    public Booking confirmBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Impossible de confirmer une réservation qui n'est pas en attente");
        }

        // Mettre à jour le statut
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());

        // Sauvegarder la réservation
        booking = bookingRepository.save(booking);

        // Envoyer un email de confirmation
        emailService.sendBookingConfirmedConfirmation(booking);

        return booking;
    }

    @Transactional
    @CacheEvict(value = {"bookings", "tourBookings", "customerBookings"}, allEntries = true)
    public Booking cancelBooking(UUID bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new RuntimeException("Impossible d'annuler une réservation déjà annulée ou expirée");
        }

        // Mettre à jour le statut
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(reason);

        // Rembourser le paiement si nécessaire
        if (booking.getPayment() != null && booking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            paymentService.refundPayment(booking.getPayment().getId());
        }

        // Sauvegarder la réservation
        booking = bookingRepository.save(booking);

        // Envoyer un email d'annulation
        emailService.sendBookingCancelledConfirmation(booking);

        return booking;
    }

    @Cacheable(value = "bookings", key = "#bookingId")
    public Booking getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + bookingId));
    }

    @Cacheable(value = "tourBookings", key = "#tourId")
    public List<Booking> getBookingsByTour(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour non trouvé avec l'ID : " + tourId));

        return bookingRepository.findByTour(tour);
    }

    @Cacheable(value = "customerBookings", key = "#email")
    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }

    // Tâche planifiée pour expirer les réservations en attente
    @Scheduled(fixedRate = 300000) // Exécuter toutes les 5 minutes
    @Transactional
    public void expirePendingBookings() {
        List<Booking> expiredBookings = bookingRepository.findExpiredPendingBookings(LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);

            // Envoyer un email d'expiration
            emailService.sendBookingExpiredNotification(booking);
        }
    }
}
