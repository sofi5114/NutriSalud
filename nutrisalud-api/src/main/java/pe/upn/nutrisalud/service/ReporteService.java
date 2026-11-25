package pe.upn.nutrisalud.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ControlBajo;
import pe.upn.nutrisalud.dto.PacienteAltitud;
import pe.upn.nutrisalud.exception.ReglaNegocioException;
import pe.upn.nutrisalud.repository.ControlRepository;
import pe.upn.nutrisalud.repository.PacienteRepository;

@Service
public class ReporteService {

    private final ProgramaService programaService;
    private final ControlRepository controles;
    private final PacienteRepository pacientes;

    public ReporteService(ProgramaService programaService, ControlRepository controles,
                          PacienteRepository pacientes) {
        this.programaService = programaService;
        this.controles = controles;
        this.pacientes = pacientes;
    }

    @Transactional(readOnly = true)
    public List<PacienteAltitud> pacientesPorAltitud(Long programaId, int altitudMin, int altitudMax) {
        programaService.obtenerPrograma(programaId);
        if (altitudMin > altitudMax) {
            throw new ReglaNegocioException("altitudMin no puede ser mayor que altitudMax");
        }
        return pacientes.enProgramaEntreAltitudes(programaId, altitudMin, altitudMax);
    }

    // S14 · práctica 2: total de alertas
    @Transactional(readOnly = true)
    public long contarAlertas(BigDecimal limite) {
        if (limite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El límite de hemoglobina debe ser mayor que 0");
        }
        return controles.contarAlertas(limite);
    }

    @Transactional(readOnly = true)
    public List<ControlBajo> bajosEnPrograma(Long programaId, BigDecimal limite,
                                             LocalDate desde) {
        if (desde.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha desde no puede estar en el futuro");
        }
        programaService.obtenerPrograma(programaId);          // 404 si no existe
        return controles.bajosEnPrograma(programaId, limite, desde);
    }
}
