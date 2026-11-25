package pe.upn.nutrisalud.dto;

import java.util.List;

/** Respuesta: los ids creados y los programas del niño. */
public record TamizajeResumen(Long pacienteId, Long controlId,
                              List<String> programas) {
}
