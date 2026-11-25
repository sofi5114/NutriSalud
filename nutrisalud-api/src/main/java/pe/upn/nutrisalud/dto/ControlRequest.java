package pe.upn.nutrisalud.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

/** Un control. Los rangos son los mismos CHECK de la tabla control. */
public record ControlRequest(
        @NotNull(message = "La fecha del control es obligatoria")
        @PastOrPresent(message = "La fecha del control no puede estar en el futuro")
        LocalDate fecha,

        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.5", message = "El peso mínimo es 0.5 kg")
        @DecimalMax(value = "40", message = "El peso máximo es 40 kg")
        BigDecimal peso,

        @NotNull(message = "La talla es obligatoria")
        @DecimalMin(value = "30", message = "La talla mínima es 30 cm")
        @DecimalMax(value = "130", message = "La talla máxima es 130 cm")
        BigDecimal talla,

        @NotNull(message = "La hemoglobina es obligatoria")
        @DecimalMin(value = "3", message = "La hemoglobina mínima es 3 g/dL")
        @DecimalMax(value = "25", message = "La hemoglobina máxima es 25 g/dL")
        BigDecimal hemoglobina) {
}
