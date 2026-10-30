package co.edu.unbosque.shopflow.notificaciones;

/** Producto del Factory Method: una notificación lista para enviarse por su canal. */
public interface Notificacion {
    void enviar(String destino, String mensaje);
}
