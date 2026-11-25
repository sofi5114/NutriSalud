package pe.upn.nutrisalud.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ProgramaResumen;
import pe.upn.nutrisalud.exception.RecursoNoEncontradoException;
import pe.upn.nutrisalud.exception.ReglaNegocioException;
import pe.upn.nutrisalud.model.Paciente;
import pe.upn.nutrisalud.model.ProgramaSocial;
import pe.upn.nutrisalud.repository.ProgramaSocialRepository;

@Service
public class ProgramaService {

    private final PacienteService pacienteService;    // obtener(id), S8
    private final ProgramaSocialRepository programas;

    public ProgramaService(PacienteService pacienteService,
                           ProgramaSocialRepository programas) {
        this.pacienteService = pacienteService;
        this.programas = programas;
    }

    /** Solo lee, pero NECESITA transacción: la colección es LAZY. */
    @Transactional(readOnly = true)
    public List<ProgramaResumen> programasDe(Long pacienteId) {
        Paciente p = pacienteService.obtener(pacienteId);
        return resumir(p);
    }

    /** No hay save(): p está administrado y JPA guarda el cambio al terminar. */
    @Transactional
    public List<ProgramaResumen> inscribir(Long pacienteId, Long programaId) {
        Paciente p = pacienteService.obtener(pacienteId);
        p.inscribir(obtenerPrograma(programaId));
        return resumir(p);
    }

    @Transactional
    public List<ProgramaResumen> retirar(Long pacienteId, Long programaId) {
        Paciente p = pacienteService.obtener(pacienteId);
        ProgramaSocial programa = obtenerPrograma(programaId);
        if (!p.retirar(programa)) {
            throw new ReglaNegocioException(
                    "El paciente " + pacienteId + " no está inscrito en el programa " + programaId);
        }
        return resumir(p);
    }

    public ProgramaSocial obtenerPrograma(Long id) {
        return programas.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Programa " + id + " no existe"));
    }

    private static List<ProgramaResumen> resumir(Paciente p) {
        return p.getProgramas().stream()         // aquí JPA lee paciente_programa
                .map(ProgramaResumen::de)
                .sorted(Comparator.comparing(ProgramaResumen::nombre))
                .toList();
    }
}
