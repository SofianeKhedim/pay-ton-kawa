package com.example.clientapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

/**
 * Configuration spécifique au profil de production.
 */
@Configuration
@Profile("prod")
public class ProdConfig {

    private static final Logger logger = LoggerFactory.getLogger(ProdConfig.class);

    @PostConstruct
    public void init() {
        logger.info("🏭 Configuration de production activée");
        logger.warn("⚠️ Aucune donnée de test ne sera initialisée");
        logger.info("🗄️ Utilisation de PostgreSQL en production");
    }
}