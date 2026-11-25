package pe.upn.nutrisalud.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.dto.ProgramaResumen;
import pe.upn.nutrisalud.service.ProgramaService;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/programas")
@Tag(name = "Programas del paciente")
public class PacienteProgramaController {

    private final ProgramaService servicio;

    public PacienteProgramaController(ProgramaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping                                      // 200 | 404
    public ResponseEntity<Jsend<List<ProgramaResumen>>> listar(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(Jsend.success(servicio.programasDe(pacienteId)));
    }

    @PostMapping("/{programaId}")                    // 200 | 401 | 404
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<Jsend<List<ProgramaResumen>>> inscribir(
            @PathVariable Long pacienteId, @PathVariable Long programaId) {
        return ResponseEntity.ok(Jsend.success(servicio.inscribir(pacienteId, programaId)));
    }

    @DeleteMapping("/{programaId}")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<Jsend<List<ProgramaResumen>>> retirar(
            @PathVariable Long pacienteId, @PathVariable Long programaId) {
        return ResponseEntity.ok(Jsend.success(servicio.retirar(pacienteId, programaId)));
    }
}
