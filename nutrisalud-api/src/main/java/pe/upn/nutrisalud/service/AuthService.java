package pe.upn.nutrisalud.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.LoginRequest;
import pe.upn.nutrisalud.dto.LoginResponse;
import pe.upn.nutrisalud.exception.CredencialesInvalidasException;
import pe.upn.nutrisalud.model.Usuario;
import pe.upn.nutrisalud.repository.UsuarioRepository;
import pe.upn.nutrisalud.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /**
     * El mismo mensaje para "no existe" y "clave incorrecta":
     * así nadie puede averiguar qué correos están registrados.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarios.findByEmail(req.correo().trim().toLowerCase())
                .orElseThrow(CredencialesInvalidasException::new);
        if (!u.estaActivo() || !encoder.matches(req.clave(), u.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        return new LoginResponse(jwt.generar(u), u.getRol().getNombre());
    }
}
