package com.tourflow.util;

import com.tourflow.dto.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationUtil {

    /**
     * Convertit une Spring Data Page en PaginatedResponse
     */
    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /**
     * Crée une requête optimisée pour la pagination avec des jointures FETCH
     */
    public static String createOptimizedQuery(String entityName, String... fetchJoins) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT e FROM ").append(entityName).append(" e");

        for (String join : fetchJoins) {
            query.append(" LEFT JOIN FETCH e.").append(join);
        }

        return query.toString();
    }

    /**
     * Crée une requête de comptage optimisée pour la pagination
     */
    public static String createCountQuery(String entityName) {
        return "SELECT COUNT(DISTINCT e) FROM " + entityName + " e";
    }
}
