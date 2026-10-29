package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import java.math.BigDecimal;
import java.util.List;

/** JSON de salida de /api/pedidos. */
public record PedidoResponse(
        String id,
        EstadoPedido estado,
        String clienteId,
        String pais,
        String contacto,
        List<ItemPedido> items,
        String direccionEnvio,
        boolean recogerEnTienda,
        boolean envioExpress,
        String cupon,
        String notaRegalo,
        String canalNotificacion,
        BigDecimal subtotal,
        BigDecimal descuento,
        Comprobante comprobante,
        String autorizacionPago) {

    public static PedidoResponse de(Pedido p) {
        return new PedidoResponse(p.id(), p.estado(), p.clienteId(), p.pais(), p.contacto(), p.items(),
                p.direccionEnvio(), p.recogerEnTienda(), p.envioExpress(), p.cupon(), p.notaRegalo(),
                p.canalNotificacion(), p.subtotal(), p.descuento(), p.comprobante(), p.autorizacionPago());
    }
}
