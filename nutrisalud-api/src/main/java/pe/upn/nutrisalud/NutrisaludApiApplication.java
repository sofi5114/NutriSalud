package pe.upn.nutrisalud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * NutriSalud · API base de la Semana 12 (SIST1402A · UPN).
 *
 * Es la API al terminar la Semana 11: lista de pacientes paginada, documentación con Swagger
 * y pruebas en pruebas/pacientes.http. Hoy se le agregan relaciones muchos a muchos,
 * transacciones y consultas JPQL con parámetros nombrados. Busca los comentarios "SEMANA 12".
 *
 * Se excluye UserDetailsServiceAutoConfiguration porque el login es propio (JWT):
 * así Spring no crea el usuario "user" con una clave al azar en la consola.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class NutrisaludApiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NutrisaludApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(NutrisaludApiApplication.class, args);
    }
}
