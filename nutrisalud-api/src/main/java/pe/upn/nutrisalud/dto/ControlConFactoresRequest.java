package pe.upn.nutrisalud.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ControlConFactoresRequest(
        @NotNull(message = "El control es obligatorio")
        @Valid
        ControlRequest control,

        @NotNull(message = "La lista de factores es obligatoria")
        List<Long> factorIds) {
}
