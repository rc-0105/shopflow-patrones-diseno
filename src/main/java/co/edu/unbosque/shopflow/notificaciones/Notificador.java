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

    /** Valida el destino con la misma notificación que se enviará, para rechazarlo antes de cobrar. */
    public final void validarDestino(String destino) {
        crearNotificacion().validarDestino(destino);
    }

    public final void notificarPagoAprobado(String pedidoId, String destino, BigDecimal totalPagado) {
        String mensaje = "ShopFlow: el pago del pedido " + pedidoId + " fue aprobado. Total pagado: "
                + totalPagado.toPlainString() + " USD.";
        crearNotificacion().enviar(destino, mensaje);
    }
}
