package pe.upn.nutrisalud.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ControlRequest;
import pe.upn.nutrisalud.dto.PacienteResumen;
import pe.upn.nutrisalud.dto.TamizajeRequest;
import pe.upn.nutrisalud.dto.TamizajeResumen;
import pe.upn.nutrisalud.exception.ReglaNegocioException;
import pe.upn.nutrisalud.model.Control;
import pe.upn.nutrisalud.model.Paciente;
import pe.upn.nutrisalud.model.ProgramaSocial;
import pe.upn.nutrisalud.repository.ControlRepository;

@Service
public class TamizajeService {

    private final PacienteService pacienteService;
    private final ProgramaService programaService;
    private final ControlRepository controles;

    public TamizajeService(PacienteService pacienteService,
                           ProgramaService programaService,
                           ControlRepository controles) {
        this.pacienteService = pacienteService;
        this.programaService = programaService;
        this.controles = controles;
    }

    /** Ficha de tamizaje: TODO o NADA. Si un paso falla, la base queda como estaba. */
    @Transactional
    public TamizajeResumen registrar(TamizajeRequest r) {
        // 1. El niño (reutiliza crear: si el DNI ya existe -> 409)
        PacienteResumen creado = pacienteService.crear(r.paciente());
        Paciente paciente = pacienteService.obtener(creado.id());

        // 2. Sus programas (si uno no existe -> 404 y se deshace el paso 1)
        for (Long programaId : r.programaIds()) {
            paciente.inscribir(programaService.obtenerPrograma(programaId));
        }

        // 3. Su primer control
        ControlRequest c = r.control();
        if (c.fecha().isBefore(paciente.getFechaNacimiento())) {
            throw new ReglaNegocioException("El control no puede ser anterior al nacimiento");
        }
        Control control = controles.save(
                new Control(paciente, c.fecha(), c.peso(), c.talla(), c.hemoglobina()));

        return new TamizajeResumen(paciente.getId(), control.getId(),
                paciente.getProgramas().stream()
                        .map(ProgramaSocial::getNombre).sorted().toList());
    }
}
