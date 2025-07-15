package com.example.clientapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

/**
 * Configuration spécifique au profil de développement.
 */
@Configuration
@Profile("dev")
public class DevConfig {

    private static final Logger logger = LoggerFactory.getLogger(DevConfig.class);

    @PostConstruct
    public void init() {
        logger.info("🔧 Configuration de développement activée");
        logger.info("📚 Base de données H2 Console disponible: http://localhost:8081/h2-console");
        logger.info("🔑 JDBC URL: jdbc:h2:mem:testdb");
        logger.info("👤 Username: sa");
        logger.info("🔓 Password: password");
        logger.info("📖 Swagger UI: http://localhost:8081/swagger-ui.html");
    }
}
