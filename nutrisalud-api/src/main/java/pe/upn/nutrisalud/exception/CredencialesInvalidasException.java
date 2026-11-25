package pe.upn.nutrisalud.exception;

/** Correo o clave incorrectos, o cuenta inactiva → 401. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Usuario o clave incorrectos");
    }
}
