package com.example.clientapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;

/**
 * Configuration de la base de données.
 *
 * Cette classe configure les aspects liés à la persistance des données,
 * incluant JPA, les repositories et la gestion des transactions.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.clientapi.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @PostConstruct
    public void init() {
        logger.info("Configuration de la base de données initialisée");
        logger.info("JPA Repositories activés dans le package: com.example.clientapi.repository");
        logger.info("Gestion des transactions activée");
    }
}