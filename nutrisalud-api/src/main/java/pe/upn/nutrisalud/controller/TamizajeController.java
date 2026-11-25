package pe.upn.nutrisalud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.dto.TamizajeRequest;
import pe.upn.nutrisalud.dto.TamizajeResumen;
import pe.upn.nutrisalud.service.TamizajeService;

@RestController
@RequestMapping("/api/tamizajes")
@Tag(name = "Tamizaje")
public class TamizajeController {

    private final TamizajeService servicio;

    public TamizajeController(TamizajeService servicio) {
        this.servicio = servicio;
    }

    @PostMapping                              // 201 | 400 | 401 | 404 | 409
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<Jsend<TamizajeResumen>> registrar(
            @Valid @RequestBody TamizajeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Jsend.success(servicio.registrar(req)));
    }
}
