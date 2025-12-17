package com.tourflow.controller;

import com.tourflow.model.Tour;
import com.tourflow.model.User;
import com.tourflow.service.AuthenticationService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours")
@Tag(name = "Tours", description = "API pour la gestion des tours")
public class TourController {

    @Autowired
    private TourService tourService;

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Créer un nouveau tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tour créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @PostMapping
    public ResponseEntity<?> createTour(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Tour tour) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Créer le tour
            Tour createdTour = tourService.createTour(tour, guide);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdTour);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Mettre à jour un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tour mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTour(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID du tour à mettre à jour") @PathVariable UUID id,
            @RequestBody Tour tourDetails) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Mettre à jour le tour
            Tour updatedTour = tourService.updateTour(id, tourDetails, guide);

            return ResponseEntity.ok(updatedTour);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Supprimer un tour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tour supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTour(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID du tour à supprimer") @PathVariable UUID id) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Supprimer le tour
            tourService.deleteTour(id, guide);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Tour supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir un tour par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tour trouvé"),
            @ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTourById(
            @Parameter(description = "ID du tour") @PathVariable UUID id) {
        try {
            Tour tour = tourService.getTourById(id);
            return ResponseEntity.ok(tour);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Obtenir tous les tours actifs")
    @ApiResponse(responseCode = "200", description = "Liste des tours actifs")
    @GetMapping
    public ResponseEntity<List<Tour>> getAllActiveTours() {
        List<Tour> tours = tourService.getAllActiveTours();
        return ResponseEntity.ok(tours);
    }

    @Operation(summary = "Obtenir les tours d'un guide")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tours du guide"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @GetMapping("/my-tours")
    public ResponseEntity<?> getMyTours(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Obtenir les tours du guide
            List<Tour> tours = tourService.getToursByGuide(guide);

            return ResponseEntity.ok(tours);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Rechercher des tours disponibles pour une période donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tours disponibles"),
            @ApiResponse(responseCode = "400", description = "Période invalide")
    })
    @GetMapping("/available")
    public ResponseEntity<?> findAvailableTours(
            @Parameter(description = "Date de début (format ISO)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (format ISO)") @RequestParam String endDate) {
        try {
            // Convertir les dates
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);

            // Rechercher les tours disponibles
            List<Tour> tours = tourService.findAvailableTours(start, end);

            return ResponseEntity.ok(tours);
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
