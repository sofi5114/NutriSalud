package pe.upn.nutrisalud.exception;

/** Los datos son válidos, pero chocan con una regla del negocio (por ejemplo, DNI repetido) → 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
