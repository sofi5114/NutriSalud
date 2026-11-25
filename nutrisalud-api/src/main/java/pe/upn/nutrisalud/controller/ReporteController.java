package pe.upn.nutrisalud.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import pe.upn.nutrisalud.dto.ControlBajo;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.dto.PacienteAltitud;
import pe.upn.nutrisalud.service.FactorService;
import pe.upn.nutrisalud.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes")
public class ReporteController {

    private final ReporteService servicio;
    private final FactorService factorService;

    public ReporteController(ReporteService servicio, FactorService factorService) {
        this.servicio = servicio;
        this.factorService = factorService;
    }

    // S14 · práctica 2: lo consume nutrisalud-indicadores (8081)
    @GetMapping("/alertas")                                         // 200 | 400 | 401 | 409
    public ResponseEntity<Jsend<Map<String, Object>>> alertas(
            @RequestParam(defaultValue = "11.0") BigDecimal limite) {
        long total = servicio.contarAlertas(limite);
        return ResponseEntity.ok(Jsend.success(Map.<String, Object>of("totalAlertas", total, "limite", limite)));
    }

    @GetMapping("/programas/{programaId}/controles-bajos")       // 200 | 400 | 404
    public ResponseEntity<Jsend<List<ControlBajo>>> bajosEnPrograma(
            @PathVariable Long programaId,
            @RequestParam BigDecimal limite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        return ResponseEntity.ok(
                Jsend.success(servicio.bajosEnPrograma(programaId, limite, desde)));
    }

    @GetMapping("/programas/{programaId}/pacientes")
    public ResponseEntity<Jsend<List<PacienteAltitud>>> porAltitud(
            @PathVariable Long programaId,
            @RequestParam(defaultValue = "0") int altitudMin,
            @RequestParam(defaultValue = "5000") int altitudMax) {
        return ResponseEntity.ok(
                Jsend.success(servicio.pacientesPorAltitud(programaId, altitudMin, altitudMax)));
    }

    @GetMapping("/factores/{factorId}/controles-bajos")
    public ResponseEntity<Jsend<List<ControlBajo>>> bajosConFactor(
            @PathVariable Long factorId,
            @RequestParam BigDecimal limite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        return ResponseEntity.ok(
                Jsend.success(factorService.controlesBajos(factorId, limite, desde)));
    }
}
