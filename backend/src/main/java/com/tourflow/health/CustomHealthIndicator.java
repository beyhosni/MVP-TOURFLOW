package com.tourflow.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        Map<String, Object> details = new HashMap<>();

        // Vérification de la base de données
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                details.put("database", "UP");
                builder.up();
            } else {
                details.put("database", "DOWN");
                builder.down();
            }
        } catch (Exception e) {
            details.put("database", "DOWN");
            details.put("databaseError", e.getMessage());
            builder.down();
        }

        // Vérification de Redis
        try {
            redisTemplate.opsForValue().get("health-check");
            details.put("redis", "UP");
        } catch (Exception e) {
            details.put("redis", "DOWN");
            details.put("redisError", e.getMessage());
            builder.down();
        }

        // Vérification de la mémoire JVM
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        Map<String, Object> memoryDetails = new HashMap<>();
        memoryDetails.put("max", maxMemory / (1024 * 1024) + " MB");
        memoryDetails.put("total", totalMemory / (1024 * 1024) + " MB");
        memoryDetails.put("used", usedMemory / (1024 * 1024) + " MB");
        memoryDetails.put("usagePercent", String.format("%.2f%%", memoryUsagePercent));

        details.put("memory", memoryDetails);

        if (memoryUsagePercent > 90) {
            builder.status("WARNING");
        }

        return builder.withDetails(details).build();
    }
}
