package com.av2dac.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Configura a documentação da API com Swagger/OpenAPI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation") // Título da documentação da API
                        .version("v1"))            // Versão da API
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication")) // Define o esquema de segurança 'Bearer Authentication'
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")  // Nome do esquema de segurança
                                        .type(SecurityScheme.Type.HTTP) // Define o tipo como HTTP
                                        .scheme("bearer")               // Usa o esquema 'bearer' para autenticação
                                        .bearerFormat("JWT")));         // Define o formato como JWT
    }
}
