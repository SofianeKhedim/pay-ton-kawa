package com.example.clientapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration de la documentation OpenAPI (Swagger).
 *
 * Cette classe configure la documentation automatique de l'API REST
 * accessible via Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PayeTonKawa - Client API")
                        .version("1.0.0")
                        .description("API REST pour la gestion des utilisateurs dans l'architecture microservices PayeTonKawa. " +
                                "Cette API permet de gérer l'ensemble du cycle de vie des utilisateurs : création, consultation, " +
                                "mise à jour, suppression et recherche avancée. Supporte les rôles CLIENT et ADMIN.")
                        .contact(new Contact()
                                .name("Équipe de développement PayeTonKawa")
                                .email("dev@payetonkawa.com")
                                .url("https://www.payetonkawa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.payetonkawa.com")
                                .description("Serveur de production")
                ));
    }
}