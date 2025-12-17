package com.tourflow.interceptor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class FetchStrategyInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FetchStrategyInterceptor.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Optimise une requête HQL en ajoutant les fetch joins appropriés
     */
    public Query optimizeQuery(String hql, String... fetchJoins) {
        String optimizedHql = hql;

        // Si la requête ne contient pas déjà des fetch joins, les ajouter
        if (!hql.contains("FETCH") && fetchJoins.length > 0) {
            // Trouver la position du FROM pour insérer les fetch joins
            int fromIndex = hql.toLowerCase().indexOf("from");
            if (fromIndex != -1) {
                String entityName = hql.substring(fromIndex + 4).trim().split(" ")[0];
                StringBuilder builder = new StringBuilder(hql);

                // Ajouter les fetch joins
                for (String join : fetchJoins) {
                    builder.append(" LEFT JOIN FETCH ").append(entityName).append(".").append(join);
                }

                optimizedHql = builder.toString();
                logger.debug("Requête optimisée avec fetch joins: {}", optimizedHql);
            }
        }

        // Limiter le nombre de résultats pour éviter les problèmes de mémoire
        if (!optimizedHql.toLowerCase().contains("limit") && !optimizedHql.toLowerCase().contains("top")) {
            optimizedHql += " LIMIT 1000";
            logger.debug("Ajout de LIMIT pour la requête: {}", optimizedHql);
        }

        return entityManager.createQuery(optimizedHql);
    }

    /**
     * Détecte les requêtes N+1 potentielles dans les logs
     */
    public void detectNPlusOneQueries(String query) {
        // Patterns pour détecter les requêtes N+1
        List<Pattern> nPlusOnePatterns = Arrays.asList(
            Pattern.compile("SELECT.*FROM.*WHERE.*id.*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("SELECT.*FROM.*WHERE.*IN.*\(.*\)", Pattern.CASE_INSENSITIVE)
        );

        for (Pattern pattern : nPlusOnePatterns) {
            if (pattern.matcher(query).find()) {
                logger.warn("Requête N+1 potentielle détectée: {}", query);
            }
        }
    }
}
