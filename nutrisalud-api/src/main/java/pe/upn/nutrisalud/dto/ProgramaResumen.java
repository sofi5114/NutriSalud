package pe.upn.nutrisalud.dto;

import pe.upn.nutrisalud.model.ProgramaSocial;

/** Lo que la API devuelve: nunca la entidad. */
public record ProgramaResumen(Long id, String nombre) {

    public static ProgramaResumen de(ProgramaSocial p) {
        return new ProgramaResumen(p.getId(), p.getNombre());
    }
}
