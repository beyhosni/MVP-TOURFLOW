package com.tourflow.controller;

import com.tourflow.dto.BookingRequest;
import com.tourflow.model.Booking;
import com.tourflow.model.User;
import com.tourflow.service.AuthenticationService;
import com.tourflow.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Réservations", description = "API pour la gestion des réservations")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Créer une nouvelle réservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou créneau non disponible"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            // Créer la réservation
            Booking booking = bookingService.createBooking(
                    request.getTourId(),
                    request.getStartDate(),
                    request.getParticipants(),
                    request.getCustomerName(),
                    request.getCustomerEmail(),
                    request.getCustomerPhone()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Confirmer une réservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation confirmée avec succès"),
            @ApiResponse(responseCode = "400", description = "Impossible de confirmer cette réservation"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de la réservation") @PathVariable UUID id) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Confirmer la réservation
            Booking booking = bookingService.confirmBooking(id);

            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Annuler une réservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation annulée avec succès"),
            @ApiResponse(responseCode = "400", description = "Impossible d'annuler cette réservation"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de la réservation") @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Annuler la réservation
            Booking booking = bookingService.cancelBooking(id, request.get("reason"));

            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir une réservation par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @Parameter(description = "ID de la réservation") @PathVariable UUID id) {
        try {
            Booking booking = bookingService.getBookingById(id);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Obtenir les réservations d'un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations du tour"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<?> getBookingsByTour(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID du tour") @PathVariable UUID tourId) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Obtenir les réservations du tour
            List<Booking> bookings = bookingService.getBookingsByTour(tourId);

            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir les réservations d'un client par son email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations du client"),
            @ApiResponse(responseCode = "400", description = "Email invalide")
    })
    @GetMapping("/customer/{email}")
    public ResponseEntity<?> getBookingsByCustomerEmail(
            @Parameter(description = "Email du client") @PathVariable String email) {
        try {
            List<Booking> bookings = bookingService.getBookingsByCustomerEmail(email);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Méthode utilitaire pour extraire le token du header Authorization
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
