package pe.upn.nutrisalud.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lee "Authorization: Bearer <token>" en cada petición. Si el token es válido, deja
 * autenticado al usuario con su rol; si no hay token o no es válido, la petición sigue
 * SIN usuario y las reglas de SecurityConfig deciden (401 si la ruta lo exige).
 *
 * No lleva @Component a propósito: se crea en SecurityConfig para que no se registre dos veces.
 */
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtFiltro(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain cadena)
            throws ServletException, IOException {
        String cabecera = req.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                Claims datos = jwt.leer(cabecera.substring(7));
                var rol = new SimpleGrantedAuthority("ROLE_" + datos.get("rol", String.class));
                var usuario = new UsernamePasswordAuthenticationToken(datos.getSubject(), null, List.of(rol));
                SecurityContextHolder.getContext().setAuthentication(usuario);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();     // token inventado, alterado o vencido
            }
        }
        cadena.doFilter(req, res);
    }
}
