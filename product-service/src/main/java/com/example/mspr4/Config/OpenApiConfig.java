package com.example.mspr4.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("API Produit")
                        .description("Documentation de l'API produit")
                        .version("1.0"));
    }
}

