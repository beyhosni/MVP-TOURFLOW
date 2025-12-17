package com.tourflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Configuration
@EnableJpaRepositories(basePackages = "com.tourflow.repository")
@EnableTransactionManagement
public class JpaInterceptorConfig {

    @PersistenceContext
    private EntityManager entityManager;

    // Configuration pour optimiser les requêtes JPA
    public class EntityListener {

        @PrePersist
        public void prePersist(Object entity) {
            // Logique avant la persistance
        }

        @PreUpdate
        public void preUpdate(Object entity) {
            // Logique avant la mise à jour
        }

        @PostLoad
        public void postLoad(Object entity) {
            // Logique après le chargement
        }
    }
}
