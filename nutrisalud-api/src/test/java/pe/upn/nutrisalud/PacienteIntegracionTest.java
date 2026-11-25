package pe.upn.nutrisalud;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

// S14 · práctica 3: pruebas de integración contra nutrisalud_test
@SpringBootTest                       // contexto completo de Spring
@AutoConfigureMockMvc                 // MockMvc: HTTP sin abrir el 8080
@ActiveProfiles("test")               // application-test.properties
@Transactional                        // rollback al final de cada prueba
class PacienteIntegracionTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    // clave desde .env.properties, no escrita aquí
    @Value("${CORREO_ENFERMERA:enfermera@posta.pe}") String correo;
    @Value("${CLAVE_ENFERMERA}") String clave;

    private String token;

    // login real antes de cada prueba
    @BeforeEach
    void iniciarSesion() throws Exception {
        String respuesta = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("correo", correo, "clave", clave))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = json.readTree(respuesta).path("data").path("token").asText();
    }

    private static final String PACIENTE_VALIDO = """
            { "dni": "", "nombres": "Prueba", "apellidos": "Integracion Test",
              "fechaNacimiento": "2025-01-10", "sexo": "F", "altitudMsnm": 34 }
            """;

    @Test
    @DisplayName("POST sin token -> 401 y JSend error")
    void crearSinToken_responde401() throws Exception {
        mvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PACIENTE_VALIDO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName("POST válido con token -> 201 y JSend success")
    void crearValido_responde201() throws Exception {
        mvc.perform(post("/api/pacientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PACIENTE_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.apellidos").value("Integracion Test"));
    }

    @Test
    @DisplayName("POST inválido con token -> 400 y JSend fail con el campo que falló")
    void crearInvalido_responde400() throws Exception {
        mvc.perform(post("/api/pacientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "dni": "123", "nombres": "", "apellidos": "Sin Nombre",
                                  "fechaNacimiento": "2025-01-10", "sexo": "F", "altitudMsnm": 6000 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data.nombres").exists())
                .andExpect(jsonPath("$.data.altitudMsnm").value("La altitud máxima es 5000 msnm"));
    }
}
