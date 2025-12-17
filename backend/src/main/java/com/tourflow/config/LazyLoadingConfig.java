package com.tourflow.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.hibernate.jpa.HibernatePersistenceProvider;

@Configuration
@EnableJpaRepositories(basePackages = "com.tourflow.repository")
@EntityScan(basePackages = "com.tourflow.model")
@EnableTransactionManagement
public class LazyLoadingConfig {

    @Bean
    public Statistics hibernateStatistics(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }
}
