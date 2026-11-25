package pe.upn.nutrisalud.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import pe.upn.nutrisalud.model.Control;

// S14 · traído del lab S11 para GET /api/pacientes/{id}/controles
/** Un control del historial del niño. Mismos nombres que la interfaz ControlPaciente de Angular. */
public record ControlResumen(
        Long id,
        LocalDate fecha,
        BigDecimal peso,
        BigDecimal talla,
        BigDecimal hemoglobina) {

    public static ControlResumen de(Control c) {
        return new ControlResumen(c.getId(), c.getFecha(), c.getPeso(), c.getTalla(), c.getHemoglobina());
    }
}
