package com.investtracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Multi-Tenant Investment Portfolio Tracker API",
        version = "1.0",
        description = "REST APIs for managing cross-tenant investment portfolios, including authentication, CRUD operations, and analytical dashboards."
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Standard JWT authentication. Provide the Bearer token returned by /auth/login."
)
public class OpenApiConfig {
    // Basic OpenAPI and Swagger UI configuration using Springdoc annotations.
    // The @SecurityScheme configures Swagger UI to support entering JWTs.
}
