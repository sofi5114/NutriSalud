package pe.upn.nutrisalud.exception;

/** El registro pedido no existe o fue dado de baja → 404. Es RuntimeException: hace rollback. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
