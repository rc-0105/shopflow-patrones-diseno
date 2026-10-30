package co.edu.unbosque.shopflow.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.facturacion.colombia.FabricaColombia;
import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones;
import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones.NotificacionEnviada;
import co.edu.unbosque.shopflow.notificaciones.NotificadorEmail;
import co.edu.unbosque.shopflow.notificaciones.NotificadorSms;
import co.edu.unbosque.shopflow.notificaciones.NotificadorWhatsApp;
import co.edu.unbosque.shopflow.pagos.PasarelasDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Reto C: un pedido con canal WHATSAPP se notifica sin haber modificado PedidoService. */
class PedidoServiceWhatsAppTest {

    private final ServicioCatalogo catalogo = new ServicioCatalogo() {
        private final Producto mouse = new Producto("SKU-002", "Mouse inalámbrico", new BigDecimal("25.50"));
        public Producto buscar(String sku) { return mouse; }
        public List<Producto> listar() { return List.of(mouse); }
        public Producto actualizarPrecio(String sku, BigDecimal precio) { throw new UnsupportedOperationException(); }
    };

    private final BandejaNotificaciones bandeja = new BandejaNotificaciones();
    private final PedidoService servicio = new PedidoService(
            catalogo,
            new ServicioFacturacion(List.of(new FabricaColombia())),
            new PasarelasDePago(Map.of("QUICKPAY", (monto, ref) -> ResultadoPago.aprobado("QP-1"))),
            List.of(new NotificadorEmail(bandeja), new NotificadorSms(bandeja), new NotificadorWhatsApp(bandeja)));

    private static PedidoRequest pedidoWhatsApp(String contacto) {
        return new PedidoRequest("CLI-001", "1020304050", "CO", contacto,
                List.of(new PedidoRequest.ItemRequest("SKU-002", 1)),
                null, true, null, null, false, "WHATSAPP");
    }

    @Test
    void unPedidoConCanalWhatsAppSeNotificaTrasElPago() {
        Pedido creado = servicio.crear(pedidoWhatsApp("+573001234567"));

        Pedido pagado = servicio.pagar(creado.id(), "QUICKPAY");

        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("WHATSAPP", enviada.canal());
        assertEquals("+573001234567", enviada.destino());
        assertTrue(enviada.mensaje().startsWith("✅ "));
        assertTrue(enviada.mensaje().contains(pagado.totalAPagar().toPlainString()));
    }

    @Test
    void unPedidoWhatsAppSinIndicativoEsRechazadoAlCrear() {
        assertThrows(IllegalArgumentException.class, () -> servicio.crear(pedidoWhatsApp("3001234567")));
    }
}
