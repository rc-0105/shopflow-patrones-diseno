package co.edu.unbosque.shopflow.notificaciones;

/** Producto del Factory Method: una notificación lista para enviarse por su canal. */
public interface Notificacion {
    void enviar(String destino, String mensaje);

    /** Lanza IllegalArgumentException si el destino no sirve para este canal. Por defecto acepta cualquiera. */
    default void validarDestino(String destino) { }
}
