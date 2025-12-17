package com.tourflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PaginationConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Configuration du resolver pour la pagination
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        // Taille de page par défaut
        pageableResolver.setOneIndexedParameters(true);
        pageableResolver.setFallbackPageable(PageRequest.of(0, 20));

        // Taille maximale de page pour éviter les requêtes trop lourdes
        pageableResolver.setMaxPageSize(100);

        // Configuration du resolver pour le tri
        SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
        sortResolver.setFallbackSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        resolvers.add(pageableResolver);
    }
}
