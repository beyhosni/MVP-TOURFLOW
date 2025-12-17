package com.tourflow.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class TimeoutConfig implements WebMvcConfigurer, AsyncConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // Timeout pour les requêtes asynchrones (30 secondes)
        configurer.setDefaultTimeout(30000);

        // Pool de threads pour les requêtes asynchrones
        configurer.setTaskExecutor(() -> {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);
            executor.setMaxPoolSize(20);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("async-");
            executor.setKeepAliveSeconds(60);
            executor.initialize();
            return executor;
        });
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Configuration des timeouts pour les requêtes HTTP
        return builder
                .setConnectTimeout(Duration.ofSeconds(10)) // 10 secondes pour la connexion
                .setReadTimeout(Duration.ofSeconds(30))    // 30 secondes pour la lecture
                .build();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        // Pool de threads pour les tâches planifiées
        return Executors.newScheduledThreadPool(5);
    }
}
