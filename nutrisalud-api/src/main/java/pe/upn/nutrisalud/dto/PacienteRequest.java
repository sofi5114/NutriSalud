package pe.upn.nutrisalud.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

/**
 * Lo que llega en POST y PUT. Las reglas son las mismas que valida el formulario de Angular
 * (Semana 9): el frontend valida por comodidad, la API valida por seguridad.
 */
public record PacienteRequest(

        @Pattern(regexp = "(\\d{8})?", message = "El DNI debe tener 8 dígitos (o quedar vacío si está en trámite)")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 80, message = "Los nombres admiten como máximo 80 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80, message = "Los apellidos admiten como máximo 80 caracteres")
        String apellidos,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "La fecha de nacimiento no puede estar en el futuro")
        LocalDate fechaNacimiento,

        @Pattern(regexp = "[MF]?", message = "El sexo debe ser M o F")
        String sexo,

        @NotNull(message = "La altitud es obligatoria")
        @Min(value = 0, message = "La altitud no puede ser negativa")
        @Max(value = 5000, message = "La altitud máxima es 5000 msnm")
        Integer altitudMsnm) {
}
