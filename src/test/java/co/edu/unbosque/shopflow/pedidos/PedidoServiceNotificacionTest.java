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
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelasDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private final PasarelaDePago aprueba = (monto, ref) -> ResultadoPago.aprobado("QP-1");
    private final PasarelaDePago rechaza = (monto, ref) -> ResultadoPago.rechazado("Fondos insuficientes");
    private final PedidoService servicio = new PedidoService(
            new CatalogoFijo(),
            new ServicioFacturacion(List.of(new FabricaColombia())),
            new PasarelasDePago(Map.of("QUICKPAY", aprueba, "LEGACYPAY", rechaza)),
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
    void trasUnPagoAprobadoSeNotificaPorElCanalDelPedidoConElTotalPagado() {
        Pedido creado = servicio.crear(pedidoConCanal("email"));

        Pedido pagado = servicio.pagar(creado.id(), "QUICKPAY");

        assertEquals(EstadoPedido.PAGADO, pagado.estado());
        assertEquals(1, bandeja.todas().size());
        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("EMAIL", enviada.canal());
        assertEquals("ana.perez@correo.com", enviada.destino());
        assertTrue(enviada.mensaje().contains(pagado.totalAPagar().toPlainString()));
    }

    @Test
    void unPagoRechazadoNoGeneraNotificacion() {
        Pedido creado = servicio.crear(pedidoConCanal("EMAIL"));

        assertThrows(IllegalStateException.class, () -> servicio.pagar(creado.id(), "LEGACYPAY"));

        assertTrue(bandeja.todas().isEmpty());
    }

    @Test
    void unPedidoSinCanalNoGeneraNotificacion() {
        Pedido creado = servicio.crear(pedidoConCanal(null));

        servicio.pagar(creado.id(), "QUICKPAY");

        assertTrue(bandeja.todas().isEmpty());
    }
}
