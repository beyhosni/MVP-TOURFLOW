package com.tourflow.service;

import com.tourflow.model.*;
import com.tourflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRuleRepository availabilityRuleRepository;

    @Autowired
    private AvailabilityExceptionRepository availabilityExceptionRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    public AvailabilityRule createAvailabilityRule(AvailabilityRule availabilityRule) {
        return availabilityRuleRepository.save(availabilityRule);
    }

    public AvailabilityRule updateAvailabilityRule(UUID ruleId, AvailabilityRule ruleDetails) {
        AvailabilityRule rule = availabilityRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Règle de disponibilité non trouvée avec l'ID : " + ruleId));

        rule.setDaysOfWeek(ruleDetails.getDaysOfWeek());
        rule.setStartTimes(ruleDetails.getStartTimes());
        rule.setMinBookingHours(ruleDetails.getMinBookingHours());
        rule.setMaxCapacity(ruleDetails.getMaxCapacity());
        rule.setActive(ruleDetails.isActive());

        return availabilityRuleRepository.save(rule);
    }

    public void deleteAvailabilityRule(UUID ruleId) {
        AvailabilityRule rule = availabilityRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Règle de disponibilité non trouvée avec l'ID : " + ruleId));

        rule.setActive(false);
        availabilityRuleRepository.save(rule);
    }

    public List<AvailabilityRule> getAvailabilityRulesByTour(UUID tourId) {
        return availabilityRuleRepository.findByTourId(tourId);
    }

    public AvailabilityException createAvailabilityException(AvailabilityException availabilityException) {
        return availabilityExceptionRepository.save(availabilityException);
    }

    public AvailabilityException updateAvailabilityException(UUID exceptionId, AvailabilityException exceptionDetails) {
        AvailabilityException exception = availabilityExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new RuntimeException("Exception de disponibilité non trouvée avec l'ID : " + exceptionId));

        exception.setStartDate(exceptionDetails.getStartDate());
        exception.setEndDate(exceptionDetails.getEndDate());
        exception.setReason(exceptionDetails.getReason());

        return availabilityExceptionRepository.save(exception);
    }

    public void deleteAvailabilityException(UUID exceptionId) {
        availabilityExceptionRepository.deleteById(exceptionId);
    }

    public List<AvailabilityException> getAvailabilityExceptionsByTour(UUID tourId) {
        return availabilityExceptionRepository.findByTourId(tourId);
    }

    @Transactional
    public List<LocalDateTime> getAvailableSlots(UUID tourId, LocalDateTime startDate, LocalDateTime endDate) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour non trouvé avec l'ID : " + tourId));

        List<AvailabilityRule> rules = availabilityRuleRepository.findByTourAndActiveTrue(tour);
        List<AvailabilityException> exceptions = availabilityExceptionRepository.findConflictingExceptions(tourId, startDate, endDate);

        List<Booking> bookings = bookingRepository.findConflictingBookings(
                tourId, startDate, endDate, 
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        List<LocalDateTime> availableSlots = new ArrayList<>();

        // Pour chaque jour dans la période demandée
        LocalDateTime current = startDate;
        while (current.isBefore(endDate)) {
            DayOfWeek dayOfWeek = DayOfWeek.of(current.getDayOfWeek().getValue());

            // Pour chaque règle de disponibilité
            for (AvailabilityRule rule : rules) {
                // Vérifier si le jour actuel est dans les jours actifs de la règle
                if (rule.getDaysOfWeek().contains(dayOfWeek)) {
                    // Pour chaque heure de départ possible
                    for (LocalTime startTime : rule.getStartTimes()) {
                        LocalDateTime slotStart = LocalDateTime.of(current.toLocalDate(), startTime);
                        LocalDateTime slotEnd = slotStart.plusMinutes(tour.getDurationMinutes());

                        // Vérifier que le créneau est dans la période demandée
                        if (slotStart.isBefore(endDate) && slotEnd.isAfter(startDate)) {
                            // Vérifier le délai minimum de réservation
                            if (slotStart.isAfter(LocalDateTime.now().plusHours(rule.getMinBookingHours()))) {
                                // Vérifier qu'il n'y a pas d'exception pour ce créneau
                                boolean hasException = exceptions.stream()
                                        .anyMatch(e -> !(e.getEndDate().isBefore(slotStart) || e.getStartDate().isAfter(slotEnd)));

                                if (!hasException) {
                                    // Vérifier qu'il n'y a pas de réservation conflictuelle
                                    boolean hasBooking = bookings.stream()
                                            .anyMatch(b -> !(b.getEndDate().isBefore(slotStart) || b.getStartDate().isAfter(slotEnd)));

                                    if (!hasBooking) {
                                        availableSlots.add(slotStart);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Passer au jour suivant
            current = current.plusDays(1);
        }

        return availableSlots;
    }
}
