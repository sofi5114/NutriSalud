package pe.upn.nutrisalud.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** La ficha completa: el niño, su primer control y sus programas. */
public record TamizajeRequest(

        @NotNull(message = "Faltan los datos del paciente")
        @Valid                          // valida también lo de ADENTRO
        PacienteRequest paciente,

        @NotNull(message = "Faltan los datos del control")
        @Valid
        ControlRequest control,

        @NotNull(message = "Envía la lista de programas (puede ir vacía: [])")
        List<@NotNull Long> programaIds) {
}
