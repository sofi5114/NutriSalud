package pe.upn.nutrisalud.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upn.nutrisalud.dto.ControlConFactoresRequest;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.exception.ValorSospechosoException;
import pe.upn.nutrisalud.service.ControlService;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/controles")
@Tag(name = "Controles del paciente")
public class ControlController {

    private final ControlService servicio;

    public ControlController(ControlService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<Jsend<Map<String, Long>>> registrar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody ControlConFactoresRequest req) throws ValorSospechosoException {
        Long id = servicio.registrar(pacienteId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Jsend.success(Map.of("id", id)));
    }
}
