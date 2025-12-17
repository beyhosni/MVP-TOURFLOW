package com.tourflow.controller;

import com.tourflow.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Métriques", description = "API pour consulter les métriques de performance")
public class MetricsController {

    @Autowired
    private LoggingService loggingService;

    @Operation(summary = "Obtenir les métriques de performance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métriques récupérées avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = loggingService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }
}
