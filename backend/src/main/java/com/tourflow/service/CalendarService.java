package com.tourflow.service;

import com.tourflow.model.Booking;
import com.tourflow.model.BookingStatus;
import com.tourflow.model.ExternalCalendar;
import com.tourflow.model.User;
import com.tourflow.repository.BookingRepository;
import com.tourflow.repository.ExternalCalendarRepository;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CalendarService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ExternalCalendarRepository externalCalendarRepository;

    public String generateICalCalendar(UUID guideId) {
        // Créer un nouveau calendrier
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//TourFlow//Guides Calendar//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        // Définir le fuseau horaire (Europe/Paris)
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Europe/Paris");
        VTimeZone tz = timezone.getVTimeZone();
        calendar.getComponents().add(tz);

        // Récupérer toutes les réservations confirmées pour ce guide
        List<Booking> bookings = bookingRepository.findByTour_Guide_IdAndStatus(
                guideId, BookingStatus.CONFIRMED
        );

        // Ajouter chaque réservation comme un événement
        for (Booking booking : bookings) {
            // Créer un événement pour la réservation
            VEvent event = new VEvent();

            // Définir les dates de début et de fin
            DateTime start = new DateTime(
                    ZonedDateTime.of(booking.getStartDate(), ZoneId.of("Europe/Paris")).toInstant().toEpochMilli()
            );
            DateTime end = new DateTime(
                    ZonedDateTime.of(booking.getEndDate(), ZoneId.of("Europe/Paris")).toInstant().toEpochMilli()
            );
            event.getProperties().add(new DtStart(start));
            event.getProperties().add(new DtEnd(end));

            // Définir le résumé (titre de l'événement)
            event.getProperties().add(new Summary(booking.getTour().getTitle()));

            // Définir la description
            String description = "Tour: " + booking.getTour().getTitle() + "\n" +
                    "Participants: " + booking.getParticipants() + "\n" +
                    "Client: " + booking.getCustomerName() + " (" + booking.getCustomerEmail() + ")\n" +
                    "Prix: " + booking.getTotalPrice() + " €";
            event.getProperties().add(new Description(description));

            // Définir l'emplacement
            event.getProperties().add(new Location(booking.getTour().getLocation()));

            // Définir l'UID (identifiant unique)
            event.getProperties().add(new Uid(booking.getId().toString() + "@tourflow.com"));

            // Marquer comme occupé (TRANSP: OPAQUE)
            event.getProperties().add(Transp.OPAQUE);

            // Ajouter l'événement au calendrier
            calendar.getComponents().add(event);
        }

        // Générer le contenu iCal
        try {
            StringWriter writer = new StringWriter();
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du calendrier iCal", e);
        }
    }

    public ExternalCalendar addExternalCalendar(User guide, String name, String icsUrl) {
        ExternalCalendar externalCalendar = new ExternalCalendar(name, icsUrl, guide);
        return externalCalendarRepository.save(externalCalendar);
    }

    public void updateExternalCalendar(UUID calendarId, String name, String icsUrl, User guide) {
        ExternalCalendar calendar = externalCalendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("Calendrier externe non trouvé avec l'ID : " + calendarId));

        if (!calendar.getGuide().getId().equals(guide.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce calendrier");
        }

        calendar.setName(name);
        calendar.setIcsUrl(icsUrl);

        externalCalendarRepository.save(calendar);
    }

    public void deleteExternalCalendar(UUID calendarId, User guide) {
        ExternalCalendar calendar = externalCalendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("Calendrier externe non trouvé avec l'ID : " + calendarId));

        if (!calendar.getGuide().getId().equals(guide.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce calendrier");
        }

        calendar.setActive(false);
        externalCalendarRepository.save(calendar);
    }

    public List<ExternalCalendar> getExternalCalendarsByGuide(User guide) {
        return externalCalendarRepository.findByGuideAndActiveTrue(guide);
    }

    @Transactional
    public void syncExternalCalendars() {
        // Cette méthode serait appelée périodiquement pour synchroniser les calendriers externes
        // Pour le MVP, nous allons simplement la laisser vide
        // Dans une version ultérieure, nous pourrions implémenter la synchronisation réelle
    }
}
