package pe.upn.nutrisalud.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Quién puede hacer qué (Semana 5).
 * Spring Security revisa las reglas de ARRIBA hacia ABAJO y se queda con la primera que coincide.
 * Semana 12: no cambia nada. Las rutas nuevas (/api/tamizajes, /api/reportes, …/programas)
 * caen en la regla /api/** y piden un token de PERSONAL_DE_SALUD.
 */
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /** true = los GET de pacientes responden sin token (solo para practicar en clase). */
    @Value("${nutrisalud.seguridad.lectura-abierta:false}")
    private boolean lecturaAbierta;

    @Value("${nutrisalud.cors.origenes:http://localhost:4200}")
    private List<String> origenes;

    @Bean
    public SecurityFilterChain cadena(HttpSecurity http, JwtService jwt) throws Exception {
        log.info("Lectura abierta (GET /api/pacientes sin token): {}", lecturaAbierta ? "ACTIVA" : "desactivada");

        http
            .csrf(csrf -> csrf.disable())                  // API sin sesión ni cookies: no aplica CSRF
            .cors(Customizer.withDefaults())               // usa el bean corsConfigurationSource de abajo
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) ->
                    responder(res, HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión"))
                .accessDeniedHandler((req, res, ex) ->
                    responder(res, HttpServletResponse.SC_FORBIDDEN, "Tu rol no tiene permiso para esta operación")))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();   // preflight de CORS
                auth.requestMatchers("/error").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                // Semana 11: Swagger y el JSON de OpenAPI
                auth.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                if (lecturaAbierta) {
                    auth.requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/**").permitAll();
                }
                auth.requestMatchers("/api/**").hasRole("PERSONAL_DE_SALUD");  // escrituras y todo lo demás
                auth.anyRequest().authenticated();                            // SIEMPRE al final
            })
            .addFilterBefore(new JwtFiltro(jwt), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** CORS: qué orígenes (frontends) pueden llamar a la API desde el navegador. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(origenes);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", cors);
        return fuente;
    }

    /** 401 y 403 también salen en JSend, igual que los demás errores. */
    private static void responder(HttpServletResponse res, int status, String mensaje) throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":\"error\",\"message\":\"" + mensaje + "\"}");
    }
}
