package com.tourflow.controller;

import com.tourflow.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Gestion des sessions", description = "API pour la gestion des sessions utilisateur")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Operation(summary = "Récupérer les informations d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de session récupérées"),
            @ApiResponse(responseCode = "404", description = "Session non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSessionInfo(@PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            return ResponseEntity.notFound().build();
        }

        Set<String> sessionKeys = sessionService.getSessionKeys(sessionId);
        Map<String, Object> sessionData = new HashMap<>();

        for (String key : sessionKeys) {
            String attributeName = key.substring(key.lastIndexOf(":") + 1);
            Object attributeValue = sessionService.getSessionValue(sessionId, attributeName);
            sessionData.put(attributeName, attributeValue);
        }

        response.put("sessionId", sessionId);
        response.put("attributes", sessionData);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Invalider une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session invalidée avec succès"),
            @ApiResponse(responseCode = "404", description = "Session non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> invalidateSession(@PathVariable String sessionId) {
        Map<String, String> response = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            response.put("message", "Session non trouvée");
            return ResponseEntity.notFound().build();
        }

        sessionService.invalidateSession(sessionId);
        response.put("message", "Session invalidée avec succès");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Étendre la durée de vie d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Durée de vie étendue avec succès"),
            @ApiResponse(responseCode = "404", description = "Session non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PutMapping("/{sessionId}/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> extendSession(@PathVariable String sessionId) {
        Map<String, String> response = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            response.put("message", "Session non trouvée");
            return ResponseEntity.notFound().build();
        }

        sessionService.extendSession(sessionId);
        response.put("message", "Durée de vie de la session étendue avec succès");

        return ResponseEntity.ok(response);
    }
}
