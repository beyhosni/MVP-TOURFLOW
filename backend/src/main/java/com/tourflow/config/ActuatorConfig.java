package com.tourflow.config;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@EnableWebSecurity
public class ActuatorConfig {

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public HealthIndicator dbHealthIndicator(DataSource dataSource) {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return Health.up()
                        .withDetail("database", "Available")
                        .build();
                }
            } catch (Exception e) {
                return Health.down()
                    .withDetail("database", "Unavailable")
                    .withException(e)
                    .build();
            }
            return Health.down().withDetail("database", "Unavailable").build();
        };
    }
}
