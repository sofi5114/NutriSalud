package pe.upn.nutrisalud.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import pe.upn.nutrisalud.dto.Jsend;

/**
 * Un solo lugar decide qué código HTTP y qué JSON recibe el cliente ante cada error (Semana 5).
 * Angular traduce estos códigos en mensajes para el personal (core/api/errores.ts, Semana 9).
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    /** 400 · @Valid falló: { "nombres": "Los nombres son obligatorios", ... } */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Jsend<Map<String, String>>> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Jsend.fail(campos));
    }

    /** 400 · un parámetro no tiene el tipo esperado: /api/pacientes/abc  o  ?pagina=abc */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Jsend<Map<String, String>>> tipoIncorrecto(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(Jsend.fail(Map.of(e.getName(), "no tiene un formato válido")));
    }

    /** 400 · falta un parámetro obligatorio */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Jsend<Map<String, String>>> faltaParametro(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(Jsend.fail(Map.of(e.getParameterName(), "es obligatorio")));
    }

    /** 400 · el cuerpo no es un JSON válido o una fecha viene mal escrita */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Jsend<Map<String, String>>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Jsend.fail(Map.of("cuerpo", "El JSON enviado no es válido")));
    }

    /** 404 · el registro no existe o fue dado de baja */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Jsend<Map<String, String>>> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Jsend.fail(Map.of("recurso", e.getMessage())));
    }

    /** 404 · la ruta no existe */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Jsend<Map<String, String>>> rutaInexistente(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Jsend.fail(Map.of("ruta", "No existe")));
    }

    /** 405 · la ruta existe pero no con ese método (por ejemplo, PATCH) */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Jsend<Map<String, String>>> metodo(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Jsend.fail(Map.of("metodo", e.getMethod() + " no está permitido aquí")));
    }

    /** 409 · regla del negocio. Angular muestra el texto de "regla" tal cual. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Jsend<Map<String, String>>> regla(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Jsend.fail(Map.of("regla", e.getMessage())));
    }

    @ExceptionHandler(ValorSospechosoException.class)
    public ResponseEntity<Jsend<Map<String, String>>> valorSospechoso(ValorSospechosoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Jsend.fail(Map.of("regla", e.getMessage())));
    }

    /** 409 · la base rechazó el dato (por ejemplo, el índice único del DNI o una clave foránea) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Jsend<Map<String, String>>> integridad(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Jsend.fail(Map.of("regla", "Los datos chocan con un registro existente")));
    }

    /** 401 · login fallido */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Jsend<Void>> credenciales(CredencialesInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Jsend.error(e.getMessage()));
    }

    /** 500 · cualquier otra cosa. El detalle va al log, NUNCA al cliente (Ley 29733). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Jsend<Void>> inesperado(Exception e) {
        log.error("Error no controlado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Jsend.error("Ocurrió un error en el servidor"));
    }
}
