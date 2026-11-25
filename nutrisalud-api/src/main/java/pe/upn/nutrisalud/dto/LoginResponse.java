package pe.upn.nutrisalud.dto;

/** Respuesta del login: el JWT y el rol del usuario. */
public record LoginResponse(String token, String rol) {
}
