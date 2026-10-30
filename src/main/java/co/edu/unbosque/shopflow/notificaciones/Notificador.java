package co.edu.unbosque.shopflow.notificaciones;

import java.math.BigDecimal;

/**
 * Creador del Factory Method: el proceso de notificar un pago es fijo (armar el
 * mensaje, crear la notificación y enviarla); cada subclase decide qué notificación crea.
 */
public abstract class Notificador {

    /** Canal que atiende este creador, en mayúsculas (EMAIL, SMS...). Es la clave del mapa en PedidoService. */
    public abstract String canal();

    /** Método fábrica: cada canal entrega su propia notificación. */
    protected abstract Notificacion crearNotificacion();

    public final void notificarPagoAprobado(String pedidoId, String destino, BigDecimal totalPagado) {
        String mensaje = "ShopFlow: el pago del pedido " + pedidoId + " fue aprobado. Total pagado: "
                + totalPagado.toPlainString() + " USD.";
        crearNotificacion().enviar(destino, mensaje);
    }
}
