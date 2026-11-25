package pe.upn.nutrisalud.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ControlBajo;
import pe.upn.nutrisalud.dto.FactorResumen;
import pe.upn.nutrisalud.exception.RecursoNoEncontradoException;
import pe.upn.nutrisalud.exception.ReglaNegocioException;
import pe.upn.nutrisalud.model.FactorRiesgo;
import pe.upn.nutrisalud.model.Paciente;
import pe.upn.nutrisalud.repository.ControlRepository;
import pe.upn.nutrisalud.repository.FactorRiesgoRepository;

@Service
public class FactorService {

    private final PacienteService pacienteService;
    private final FactorRiesgoRepository factores;
    private final ControlRepository controles;

    public FactorService(PacienteService pacienteService,
                         FactorRiesgoRepository factores,
                         ControlRepository controles) {
        this.pacienteService = pacienteService;
        this.factores = factores;
        this.controles = controles;
    }

    @Transactional(readOnly = true)
    public List<FactorResumen> factoresDe(Long pacienteId) {
        Paciente p = pacienteService.obtener(pacienteId);
        return resumir(p);
    }

    @Transactional
    public List<FactorResumen> agregar(Long pacienteId, Long factorId) {
        Paciente p = pacienteService.obtener(pacienteId);
        p.agregarFactor(obtenerFactor(factorId));
        return resumir(p);
    }

    public FactorRiesgo obtenerFactor(Long id) {
        return factores.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Factor " + id + " no existe"));
    }

    @Transactional(readOnly = true)
    public List<ControlBajo> controlesBajos(Long factorId, BigDecimal limite, LocalDate desde) {
        if (desde.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha desde no puede estar en el futuro");
        }
        obtenerFactor(factorId);
        return controles.bajosConFactor(factorId, limite, desde);
    }

    private static List<FactorResumen> resumir(Paciente p) {
        return p.getFactores().stream()
                .map(FactorResumen::de)
                .sorted(Comparator.comparing(FactorResumen::nombre))
                .toList();
    }
}
