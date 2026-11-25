package pe.upn.nutrisalud.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una fila del reporte. La llena JPQL con "SELECT new". */
public record ControlBajo(Long pacienteId, String nombres,
                          String apellidos, LocalDate fecha,
                          BigDecimal hemoglobina) {
}
