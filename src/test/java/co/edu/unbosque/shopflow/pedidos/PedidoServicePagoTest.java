package co.edu.unbosque.shopflow.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.facturacion.colombia.FabricaColombia;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelasDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PedidoServicePagoTest {

    private final List<BigDecimal> cobros = new ArrayList<>();

    private final ServicioCatalogo catalogo = new ServicioCatalogo() {
        private final Producto portatil = new Producto("SKU-004", "Portátil 14 pulgadas", new BigDecimal("1450.00"));
        public Producto buscar(String sku) { return portatil; }
        public List<Producto> listar() { return List.of(portatil); }
        public Producto actualizarPrecio(String sku, BigDecimal precio) { throw new UnsupportedOperationException(); }
    };

    private PasarelaDePago aprueba(String autorizacion) {
        return (monto, ref) -> {
            cobros.add(monto);
            return ResultadoPago.aprobado(autorizacion);
        };
    }

    private final PasarelaDePago sinFondos = (monto, ref) -> {
        cobros.add(monto);
        return ResultadoPago.rechazado("Fondos insuficientes");
    };

    private PedidoService servicio(Map<String, PasarelaDePago> pasarelas) {
        return new PedidoService(catalogo,
                new ServicioFacturacion(List.of(new FabricaColombia())), new PasarelasDePago(pasarelas));
    }

    private PedidoRequest pedidoDeDosPortatiles() {
        return new PedidoRequest("CLI-001", "1020304050", "CO", "ana.perez@correo.com",
                List.of(new PedidoRequest.ItemRequest("SKU-004", 2)), "Calle 134 # 9-51",
                null, null, null, null, "EMAIL");
    }

    @Test
    void cobraElTotalDelComprobanteYMarcaElPedidoComoPagado() {
        PedidoService servicio = servicio(Map.of("QUICKPAY", aprueba("QP-1")));
        Pedido creado = servicio.crear(pedidoDeDosPortatiles());

        Pedido pagado = servicio.pagar(creado.id(), "quickpay");

        assertEquals(EstadoPedido.PAGADO, pagado.estado());
        assertEquals("QP-1", pagado.autorizacionPago());
        assertEquals(List.of(creado.comprobante().total()), cobros);
        assertEquals(EstadoPedido.PAGADO, servicio.buscar(creado.id()).estado());
    }

    @Test
    void unPedidoYaPagadoResponde409SinCobrarOtraVez() {
        PedidoService servicio = servicio(Map.of("QUICKPAY", aprueba("QP-1")));
        Pedido creado = servicio.crear(pedidoDeDosPortatiles());
        servicio.pagar(creado.id(), "QUICKPAY");

        assertThrows(IllegalStateException.class, () -> servicio.pagar(creado.id(), "QUICKPAY"));
        assertEquals(1, cobros.size());
    }

    @Test
    void unRechazoResponde409YElPedidoSePuedePagarConOtroProveedor() {
        PedidoService servicio = servicio(Map.of("LEGACYPAY", sinFondos, "QUICKPAY", aprueba("QP-1")));
        Pedido creado = servicio.crear(pedidoDeDosPortatiles());

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> servicio.pagar(creado.id(), "LEGACYPAY"));
        assertEquals("Fondos insuficientes", e.getMessage());
        assertEquals(EstadoPedido.CREADO, servicio.buscar(creado.id()).estado());

        assertEquals(EstadoPedido.PAGADO, servicio.pagar(creado.id(), "QUICKPAY").estado());
    }

    @Test
    void unProveedorDesconocidoResponde400() {
        PedidoService servicio = servicio(Map.of("QUICKPAY", aprueba("QP-1")));
        Pedido creado = servicio.crear(pedidoDeDosPortatiles());

        assertThrows(IllegalArgumentException.class, () -> servicio.pagar(creado.id(), "PAYPAL"));
        assertThrows(IllegalArgumentException.class, () -> servicio.pagar(creado.id(), null));
    }
}
