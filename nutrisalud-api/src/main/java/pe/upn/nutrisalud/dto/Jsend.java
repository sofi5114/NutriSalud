package pe.upn.nutrisalud.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * El sobre de TODAS las respuestas (Semana 5).
 *   success: salió bien, el resultado va en data
 *   fail:    el cliente envió algo mal, el detalle va en data
 *   error:   falló el servidor o la sesión, la explicación va en message
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Jsend<T>(String status, T data, String message) {

    public static <T> Jsend<T> success(T data) {
        return new Jsend<>("success", data, null);
    }

    public static <T> Jsend<T> fail(T data) {
        return new Jsend<>("fail", data, null);
    }

    public static Jsend<Void> error(String message) {
        return new Jsend<>("error", null, message);
    }
}
