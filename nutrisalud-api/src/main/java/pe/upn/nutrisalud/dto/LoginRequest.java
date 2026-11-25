package pe.upn.nutrisalud.dto;

import jakarta.validation.constraints.NotBlank;

/** Lo que envía el cliente al iniciar sesión: { "correo": "...", "clave": "..." } */
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") String correo,
        @NotBlank(message = "La clave es obligatoria") String clave) {
}
