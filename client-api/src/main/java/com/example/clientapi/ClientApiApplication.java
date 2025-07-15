package com.example.clientapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application Client API pour PayeTonKawa.
 *
 * Cette application expose une API REST pour la gestion des clients dans
 * l'architecture microservices de PayeTonKawa.
 *
 * Fonctionnalités principales :
 * - CRUD complet des clients
 * - Validation des données
 * - Documentation API automatique
 * - Monitoring avec Actuator
 *
 * @author PayeTonKawa Development Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class ClientApiApplication {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(ClientApiApplication.class, args);
    }
}