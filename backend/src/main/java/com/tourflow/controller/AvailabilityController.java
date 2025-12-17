package com.tourflow.controller;

import com.tourflow.model.*;
import com.tourflow.service.AuthenticationService;
import com.tourflow.service.AvailabilityService;
import com.tourflow.service.TourService;
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
@RequestMapping("/api/availability")
@Tag(name = "Disponibilités", description = "API pour la gestion des disponibilités")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TourService tourService;

    // Gestion des règles de disponibilité

    @Operation(summary = "Créer une règle de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Règle de disponibilité créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @PostMapping("/rules")
    public ResponseEntity<?> createAvailabilityRule(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AvailabilityRule availabilityRule) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Vérifier que le tour appartient au guide
            Tour tour = tourService.getTourByIdAndGuide(availabilityRule.getTour().getId(), guide);
            availabilityRule.setTour(tour);

            // Créer la règle de disponibilité
            AvailabilityRule createdRule = availabilityService.createAvailabilityRule(availabilityRule);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdRule);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Mettre à jour une règle de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Règle de disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Règle de disponibilité non trouvée")
    })
    @PutMapping("/rules/{id}")
    public ResponseEntity<?> updateAvailabilityRule(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de la règle de disponibilité") @PathVariable UUID id,
            @RequestBody AvailabilityRule ruleDetails) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Mettre à jour la règle de disponibilité
            AvailabilityRule updatedRule = availabilityService.updateAvailabilityRule(id, ruleDetails);

            // Vérifier que le tour appartient au guide
            tourService.getTourByIdAndGuide(updatedRule.getTour().getId(), guide);

            return ResponseEntity.ok(updatedRule);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Supprimer une règle de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Règle de disponibilité supprimée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Règle de disponibilité non trouvée")
    })
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<?> deleteAvailabilityRule(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de la règle de disponibilité") @PathVariable UUID id) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Supprimer la règle de disponibilité
            availabilityService.deleteAvailabilityRule(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Règle de disponibilité supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir les règles de disponibilité d'un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des règles de disponibilité"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @GetMapping("/rules/tour/{tourId}")
    public ResponseEntity<?> getAvailabilityRulesByTour(
            @Parameter(description = "ID du tour") @PathVariable UUID tourId) {
        try {
            List<AvailabilityRule> rules = availabilityService.getAvailabilityRulesByTour(tourId);
            return ResponseEntity.ok(rules);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Gestion des exceptions de disponibilité

    @Operation(summary = "Créer une exception de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exception de disponibilité créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @PostMapping("/exceptions")
    public ResponseEntity<?> createAvailabilityException(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AvailabilityException availabilityException) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Vérifier que le tour appartient au guide
            Tour tour = tourService.getTourByIdAndGuide(availabilityException.getTour().getId(), guide);
            availabilityException.setTour(tour);

            // Créer l'exception de disponibilité
            AvailabilityException createdException = availabilityService.createAvailabilityException(availabilityException);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdException);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Mettre à jour une exception de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exception de disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Exception de disponibilité non trouvée")
    })
    @PutMapping("/exceptions/{id}")
    public ResponseEntity<?> updateAvailabilityException(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de l'exception de disponibilité") @PathVariable UUID id,
            @RequestBody AvailabilityException exceptionDetails) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Mettre à jour l'exception de disponibilité
            AvailabilityException updatedException = availabilityService.updateAvailabilityException(id, exceptionDetails);

            // Vérifier que le tour appartient au guide
            tourService.getTourByIdAndGuide(updatedException.getTour().getId(), guide);

            return ResponseEntity.ok(updatedException);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Supprimer une exception de disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exception de disponibilité supprimée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Exception de disponibilité non trouvée")
    })
    @DeleteMapping("/exceptions/{id}")
    public ResponseEntity<?> deleteAvailabilityException(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID de l'exception de disponibilité") @PathVariable UUID id) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Supprimer l'exception de disponibilité
            availabilityService.deleteAvailabilityException(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Exception de disponibilité supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir les exceptions de disponibilité d'un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des exceptions de disponibilité"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @GetMapping("/exceptions/tour/{tourId}")
    public ResponseEntity<?> getAvailabilityExceptionsByTour(
            @Parameter(description = "ID du tour") @PathVariable UUID tourId) {
        try {
            List<AvailabilityException> exceptions = availabilityService.getAvailabilityExceptionsByTour(tourId);
            return ResponseEntity.ok(exceptions);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir les créneaux disponibles pour un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des créneaux disponibles"),
            @ApiResponse(responseCode = "400", description = "Période invalide"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @GetMapping("/slots/{tourId}")
    public ResponseEntity<?> getAvailableSlots(
            @Parameter(description = "ID du tour") @PathVariable UUID tourId,
            @Parameter(description = "Date de début (format ISO)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (format ISO)") @RequestParam String endDate) {
        try {
            // Convertir les dates
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            // Obtenir les créneaux disponibles
            List<LocalDateTime> availableSlots = availabilityService.getAvailableSlots(tourId, start, end);

            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Format de date invalide. Utilisez le format ISO (yyyy-MM-ddTHH:mm:ss)");
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
