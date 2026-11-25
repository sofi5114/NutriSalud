package pe.upn.nutrisalud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upn.nutrisalud.dto.Jsend;
import pe.upn.nutrisalud.dto.LoginRequest;
import pe.upn.nutrisalud.dto.LoginResponse;
import pe.upn.nutrisalud.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthService servicio;

    public AuthController(AuthService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/login")                            // 200 | 400 | 401
    @Operation(summary = "Iniciar sesión y obtener el JWT")
    public ResponseEntity<Jsend<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(Jsend.success(servicio.login(req)));
    }
}
