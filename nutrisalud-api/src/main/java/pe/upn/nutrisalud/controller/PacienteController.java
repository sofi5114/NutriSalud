package pe.upn.nutrisalud.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upn.nutrisalud.dto.ControlResumen;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.dto.PacienteRequest;
import pe.upn.nutrisalud.dto.PacienteResumen;
import pe.upn.nutrisalud.dto.Pagina;
import pe.upn.nutrisalud.service.PacienteService;

/** CRUD de pacientes. Escribir: solo PERSONAL_DE_SALUD (regla en SecurityConfig). */
// S14 · práctica 3: los 7 endpoints documentados con @Tag, @Operation y @ApiResponse
@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Niños en seguimiento de anemia: listar, ver, registrar, editar y dar de baja")
public class PacienteController {

    private final PacienteService servicio;

    public PacienteController(PacienteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar pacientes por páginas",
            description = "Solo activos, ordenados por apellidos. tamano máximo: " + PacienteService.TAMANO_MAXIMO + ".")
    @ApiResponse(responseCode = "200", description = "Una página de pacientes (JSend success)")
    @ApiResponse(responseCode = "400", description = "pagina o tamano no son números (JSend fail)")
    public ResponseEntity<Jsend<Pagina<PacienteResumen>>> listar(
            @Parameter(description = "Número de página; la primera es 0", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Filas por página (1 a 50)", example = "10")
            @RequestParam(defaultValue = "10") int tamano,
            @Parameter(description = "Texto contenido en los apellidos", example = "Rios")
            @RequestParam(defaultValue = "") String buscar) {
        return ResponseEntity.ok(Jsend.success(servicio.listar(pagina, tamano, buscar)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un paciente con su último control")
    @ApiResponse(responseCode = "200", description = "Encontrado (JSend success)")
    @ApiResponse(responseCode = "400", description = "El id no es un número (JSend fail)")
    @ApiResponse(responseCode = "404", description = "No existe o fue dado de baja (JSend fail)")
    public ResponseEntity<Jsend<PacienteResumen>> buscar(
            @Parameter(description = "Id del paciente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(Jsend.success(servicio.buscar(id)));
    }

    // S14 · endpoint nuevo: la web S11 lo usa en el detalle del paciente
    @GetMapping("/{id}/controles")
    @Operation(summary = "Historial de controles de un paciente, por páginas",
            description = "Del control más reciente al más antiguo. Tamaño máximo: " + PacienteService.TAMANO_MAXIMO + ".")
    @ApiResponse(responseCode = "200", description = "Una página de controles (JSend success)")
    @ApiResponse(responseCode = "400", description = "pagina o tamano no son números (JSend fail)")
    @ApiResponse(responseCode = "404", description = "El paciente no existe o fue dado de baja (JSend fail)")
    public ResponseEntity<Jsend<Pagina<ControlResumen>>> listarControles(
            @Parameter(description = "Id del paciente", example = "1") @PathVariable Long id,
            @Parameter(description = "Número de página; la primera es 0", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Controles por página", example = "5")
            @RequestParam(defaultValue = "5") int tamano) {
        return ResponseEntity.ok(Jsend.success(servicio.listarControles(id, pagina, tamano)));
    }

    @PostMapping
    @Operation(summary = "Registrar un paciente", description = "Requiere token de PERSONAL_DE_SALUD.")
    @SecurityRequirement(name = "bearer")
    @ApiResponse(responseCode = "201", description = "Creado (JSend success con el paciente)")
    @ApiResponse(responseCode = "400", description = "Datos inválidos: campo → mensaje (JSend fail)")
    @ApiResponse(responseCode = "401", description = "Sin token o token inválido (JSend error)")
    @ApiResponse(responseCode = "403", description = "El rol no puede registrar (JSend error)")
    @ApiResponse(responseCode = "409", description = "DNI repetido (JSend fail)")
    public ResponseEntity<Jsend<PacienteResumen>> crear(@Valid @RequestBody PacienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Jsend.success(servicio.crear(req)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un paciente", description = "Requiere token de PERSONAL_DE_SALUD.")
    @SecurityRequirement(name = "bearer")
    @ApiResponse(responseCode = "200", description = "Actualizado (JSend success)")
    @ApiResponse(responseCode = "400", description = "Datos inválidos (JSend fail)")
    @ApiResponse(responseCode = "401", description = "Sin token o token inválido (JSend error)")
    @ApiResponse(responseCode = "403", description = "El rol no puede editar (JSend error)")
    @ApiResponse(responseCode = "404", description = "No existe (JSend fail)")
    @ApiResponse(responseCode = "409", description = "DNI repetido (JSend fail)")
    public ResponseEntity<Jsend<PacienteResumen>> actualizar(
            @Parameter(description = "Id del paciente", example = "1") @PathVariable Long id,
            @Valid @RequestBody PacienteRequest req) {
        return ResponseEntity.ok(Jsend.success(servicio.actualizar(id, req)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja a un paciente (baja lógica)", description = "No borra la fila: pone activo = 0.")
    @SecurityRequirement(name = "bearer")
    @ApiResponse(responseCode = "204", description = "Dado de baja, sin cuerpo")
    @ApiResponse(responseCode = "401", description = "Sin token o token inválido (JSend error)")
    @ApiResponse(responseCode = "403", description = "El rol no puede dar de baja (JSend error)")
    @ApiResponse(responseCode = "404", description = "No existe (JSend fail)")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del paciente", example = "1") @PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // S14 · antes no tenía documentación Swagger
    @PutMapping("/altitud")
    @Operation(summary = "Corregir la altitud de todos los pacientes que tienen un valor",
            description = "Actualización masiva: cambia altitudMsnm = anterior por nueva. Devuelve cuántos cambió.")
    @SecurityRequirement(name = "bearer")
    @ApiResponse(responseCode = "200", description = "Corregidos: { actualizados: n } (JSend success)")
    @ApiResponse(responseCode = "400", description = "Falta anterior o nueva, o no son números (JSend fail)")
    @ApiResponse(responseCode = "401", description = "Sin token o token inválido (JSend error)")
    @ApiResponse(responseCode = "409", description = "La nueva altitud está fuera de 0 a 5000 (JSend fail)")
    public ResponseEntity<Jsend<Map<String, Integer>>> corregirAltitud(
            @Parameter(description = "Altitud mal registrada", example = "34") @RequestParam int anterior,
            @Parameter(description = "Altitud correcta (0 a 5000)", example = "40") @RequestParam int nueva) {
        int filas = servicio.corregirAltitud(anterior, nueva);
        return ResponseEntity.ok(Jsend.success(Map.of("actualizados", filas)));
    }
}
