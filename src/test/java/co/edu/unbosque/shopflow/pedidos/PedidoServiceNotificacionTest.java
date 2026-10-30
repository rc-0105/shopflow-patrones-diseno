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
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PedidoServiceNotificacionTest {

    /** Catálogo falso escrito a mano: un solo producto y sin latencia. */
    static class CatalogoFijo implements ServicioCatalogo {
        private final Producto teclado = new Producto("SKU-001", "Teclado mecánico", new BigDecimal("100.00"));

        @Override
        public Producto buscar(String sku) {
            return teclado;
        }

        @Override
        public List<Producto> listar() {
            return List.of(teclado);
        }

        @Override
        public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
            throw new UnsupportedOperationException();
        }
    }

    private final BandejaNotificaciones bandeja = new BandejaNotificaciones();
    private final PedidoService servicio = new PedidoService(
            new CatalogoFijo(),
            new ServicioFacturacion(List.of(new FabricaColombia())),
            List.of(new NotificadorEmail(bandeja), new NotificadorSms(bandeja)));

    private static PedidoRequest pedidoConCanal(String canal) {
        return new PedidoRequest("CLI-001", "1020304050", "CO", "ana.perez@correo.com",
                List.of(new PedidoRequest.ItemRequest("SKU-001", 1)),
                "Calle 134 # 9-51, Bogotá", false, null, null, false, canal);
    }

    @Test
    void unCanalNoSoportadoEsRechazadoAlCrearElPedido() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(pedidoConCanal("PALOMA")));
        assertTrue(error.getMessage().contains("PALOMA"));
    }

    @Test
    void unPedidoSinCanalSeSigueCreando() {
        Pedido pedido = servicio.crear(pedidoConCanal(null));

        assertEquals(EstadoPedido.CREADO, pedido.estado());
    }

    @Test
    void trasElPagoSeNotificaPorElCanalDelPedidoConElTotalPagado() {
        Pedido pagado = servicio.crear(pedidoConCanal("email")).pagado("LP4051");

        servicio.notificarPagoAprobado(pagado);

        assertEquals(1, bandeja.todas().size());
        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("EMAIL", enviada.canal());
        assertTrue(enviada.mensaje().contains(pagado.totalAPagar().toPlainString()));
    }

    @Test
    void unPedidoSinCanalNoGeneraNotificacion() {
        Pedido pagado = servicio.crear(pedidoConCanal(null)).pagado("LP4051");

        servicio.notificarPagoAprobado(pagado);

        assertTrue(bandeja.todas().isEmpty());
    }
}
