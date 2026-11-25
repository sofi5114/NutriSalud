package pe.upn.nutrisalud.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.upn.nutrisalud.dto.FactorResumen;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.service.FactorService;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/factores")
@Tag(name = "Factores del paciente")
public class PacienteFactorController {

    private final FactorService servicio;

    public PacienteFactorController(FactorService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<Jsend<List<FactorResumen>>> listar(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(Jsend.success(servicio.factoresDe(pacienteId)));
    }

    @PostMapping("/{factorId}")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<Jsend<List<FactorResumen>>> agregar(
            @PathVariable Long pacienteId, @PathVariable Long factorId) {
        return ResponseEntity.ok(Jsend.success(servicio.agregar(pacienteId, factorId)));
    }
}
