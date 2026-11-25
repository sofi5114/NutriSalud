package pe.upn.nutrisalud.dto;

import pe.upn.nutrisalud.model.FactorRiesgo;

public record FactorResumen(Long id, String nombre) {

    public static FactorResumen de(FactorRiesgo f) {
        return new FactorResumen(f.getId(), f.getNombre());
    }
}
