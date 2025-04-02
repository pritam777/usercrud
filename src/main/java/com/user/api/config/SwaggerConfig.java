package com.user.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the User Management microservice.
 * Configures the API metadata, JWT bearer token authentication,
 * and grouped endpoint documentation.
 * 
 * Used by SpringDoc to generate Swagger UI documentation.
 * 
 * @author Pritam Singh
 */
@Configuration
public class SwaggerConfig {

    /**
     * Provides the core OpenAPI configuration including:
     * - API title, version, description
     * - JWT Bearer authentication schema
     *
     * @return configured OpenAPI bean
     */
    @Bean
    public OpenAPI apiInfo() {
    	 System.out.println("Loading Swagger/OpenAPI...");
        return new OpenAPI()
            .info(new Info()
                .title("User Management API")
                .version("1.0.0")
                .description("Spring Boot microservice for user registration, JWT authentication, and role-based access"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    /**
     * Creates a Swagger group named "user-auth" that includes all endpoints
     * matching the "/api/v1/**" path pattern.
     *
     * @return grouped OpenAPI configuration
     */
    @Bean
    public GroupedOpenApi userApis() {
        return GroupedOpenApi.builder()
            .group("user-auth")
            .pathsToMatch("/api/v1/**")
            .build();
    }
}
