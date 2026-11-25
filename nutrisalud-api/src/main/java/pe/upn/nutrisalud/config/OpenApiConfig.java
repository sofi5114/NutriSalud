package pe.upn.nutrisalud.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Semana 11: título de la documentación y el botón Authorize de Swagger. */
@Configuration
@OpenAPIDefinition(info = @Info(
    title = "NutriSalud API",
    version = "1.0",
    description = "Seguimiento de anemia infantil. "
                + "Todas las respuestas en JSend."))
@SecurityScheme(
    name = "bearer",          // el mismo nombre que usa @SecurityRequirement
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class OpenApiConfig { }    // vacía: solo anotaciones
