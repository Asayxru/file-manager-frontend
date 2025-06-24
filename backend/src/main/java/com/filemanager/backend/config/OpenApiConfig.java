package com.filemanager.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration class.
 *
 * This configuration enables JWT Bearer authentication for Swagger UI
 * and documents the security scheme for all secured endpoints.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "File Manager API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth")  // <-- Use bearerAuth by default
)
@SecurityScheme(
        name = "bearerAuth",                        // Name used in security section
        type = SecuritySchemeType.HTTP,             // HTTP authentication
        scheme = "bearer",                          // Use Bearer scheme
        bearerFormat = "JWT",                       // JWT format
        in = SecuritySchemeIn.HEADER                // Token in Authorization header
)
public class OpenApiConfig {
}
