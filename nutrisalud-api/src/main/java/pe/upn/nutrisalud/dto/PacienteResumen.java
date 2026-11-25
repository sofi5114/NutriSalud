package pe.upn.nutrisalud.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lo que la API DEVUELVE de cada paciente. Mismos nombres que la interfaz Paciente de Angular.
 * hemoglobina y fechaUltimoControl salen del último control: son null si aún no tiene controles.
 */
public record PacienteResumen(
        Long id,
        String dni,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        int edadMeses,
        Integer altitudMsnm,
        BigDecimal hemoglobina,
        LocalDate fechaUltimoControl) {
}
