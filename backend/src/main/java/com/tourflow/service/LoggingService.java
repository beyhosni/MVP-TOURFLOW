package com.tourflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final Map<String, Long> methodExecutionTimes = new HashMap<>();

    public void logMethodExecution(String methodName, long executionTime) {
        methodExecutionTimes.put(methodName, executionTime);

        if (executionTime > 1000) {
            logger.warn("Méthode lente détectée: {} - Temps d'exécution: {}ms", methodName, executionTime);
        } else {
            logger.info("Méthode exécutée: {} - Temps d'exécution: {}ms", methodName, executionTime);
        }
    }

    public void logApiCall(String endpoint, String method, long executionTime, int statusCode) {
        String logMessage = String.format("API Call - Endpoint: %s, Method: %s, Execution Time: %dms, Status: %d", 
                endpoint, method, executionTime, statusCode);

        if (executionTime > 1000) {
            logger.warn("API Call lent - {}", logMessage);
        } else {
            logger.info("API Call - {}", logMessage);
        }
    }

    public void logError(String message, Exception ex) {
        logger.error("Erreur: {}", message, ex);
    }

    public void logBusinessEvent(String event, String details) {
        logger.info("Événement métier: {} - {}", event, details);
    }

    public void logDatabaseQuery(String query, long executionTime) {
        if (executionTime > 500) {
            logger.warn("Requête base de données lente: {}ms - {}", executionTime, query);
        } else {
            logger.debug("Requête base de données: {}ms - {}", executionTime, query);
        }
    }

    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("methodExecutionTimes", methodExecutionTimes);
        metrics.put("timestamp", LocalDateTime.now());
        return metrics;
    }
}
