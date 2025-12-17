package com.tourflow.controller;

import com.tourflow.model.ExternalCalendar;
import com.tourflow.model.User;
import com.tourflow.service.AuthenticationService;
import com.tourflow.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Calendriers", description = "API pour la gestion des calendriers iCal")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Exporter le calendrier d'un guide au format iCal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calendrier iCal généré avec succès",
                    content = { @Content(mediaType = "text/calendar",
                            schema = @Schema(type = "string", format = "binary")) }),
            @ApiResponse(responseCode = "404", description = "Guide non trouvé")
    })
    @GetMapping(value = "/ics/{guideId}", produces = MediaType.TEXT_CALENDAR_VALUE)
    public ResponseEntity<String> exportICalCalendar(
            @Parameter(description = "ID du guide") @PathVariable UUID guideId) {
        try {
            String iCalCalendar = calendarService.generateICalCalendar(guideId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_CALENDAR);
            headers.setContentDispositionFormData("attachment", "tourflow-calendar.ics");

            return new ResponseEntity<>(iCalCalendar, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.toString());
        }
    }

    @Operation(summary = "Ajouter un calendrier externe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Calendrier externe ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @PostMapping("/external")
    public ResponseEntity<?> addExternalCalendar(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ExternalCalendarRequest request) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Ajouter le calendrier externe
            ExternalCalendar externalCalendar = calendarService.addExternalCalendar(
                    guide,
                    request.getName(),
                    request.getIcsUrl()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(externalCalendar);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Mettre à jour un calendrier externe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calendrier externe mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Calendrier externe non trouvé")
    })
    @PutMapping("/external/{id}")
    public ResponseEntity<?> updateExternalCalendar(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID du calendrier externe") @PathVariable UUID id,
            @RequestBody ExternalCalendarRequest request) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Mettre à jour le calendrier externe
            calendarService.updateExternalCalendar(id, request.getName(), request.getIcsUrl(), guide);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Calendrier externe mis à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Supprimer un calendrier externe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calendrier externe supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Calendrier externe non trouvé")
    })
    @DeleteMapping("/external/{id}")
    public ResponseEntity<?> deleteExternalCalendar(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "ID du calendrier externe") @PathVariable UUID id) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Supprimer le calendrier externe
            calendarService.deleteExternalCalendar(id, guide);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Calendrier externe supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtenir les calendriers externes d'un guide")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des calendriers externes"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @GetMapping("/external")
    public ResponseEntity<?> getExternalCalendars(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Récupérer l'utilisateur authentifié
            User guide = authenticationService.getUserFromToken(extractToken(authorizationHeader));

            // Obtenir les calendriers externes du guide
            List<ExternalCalendar> externalCalendars = calendarService.getExternalCalendarsByGuide(guide);

            return ResponseEntity.ok(externalCalendars);
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

    // DTO pour la requête de calendrier externe
    public static class ExternalCalendarRequest {
        private String name;
        private String icsUrl;

        // Getters et Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcsUrl() {
            return icsUrl;
        }

        public void setIcsUrl(String icsUrl) {
            this.icsUrl = icsUrl;
        }
    }
}
