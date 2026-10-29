package co.edu.unbosque.shopflow.facturacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.facturacion.colombia.FabricaColombia;
import co.edu.unbosque.shopflow.facturacion.mexico.FabricaMexico;
import co.edu.unbosque.shopflow.pedidos.ItemPedido;
import co.edu.unbosque.shopflow.pedidos.Pedido;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicioFacturacionTest {

    private final ServicioFacturacion servicio =
            new ServicioFacturacion(List.of(new FabricaColombia(), new FabricaMexico()));

    private Pedido pedido(String pais, String documento) {
        return new Pedido.Builder("CLI-001", documento, pais, "ana.perez@correo.com")
                .id("PED-1")
                .item(new ItemPedido("SKU-001", "Teclado mecánico", 1, new BigDecimal("85.00")))
                .item(new ItemPedido("SKU-002", "Mouse inalámbrico", 2, new BigDecimal("25.50")))
                .direccionEnvio("Calle 134 # 9-51")
                .cupon("BOSQUE10")
                .build();
    }

    @Test
    void pedidoDeColombiaGeneraFacturaDianConIva19() {
        Comprobante c = servicio.facturar(pedido("CO", "1020304050"));

        assertEquals("Factura electrónica (DIAN)", c.tipo());
        assertEquals("IVA 19%", c.nombreImpuesto());
        assertEquals(new BigDecimal("122.40"), c.base());
        assertEquals(new BigDecimal("23.26"), c.impuesto());
        assertEquals(new BigDecimal("145.66"), c.total());
    }

    @Test
    void pedidoDeMexicoGeneraCfdiConIva16() {
        Comprobante c = servicio.facturar(pedido("MX", "GODE561231GR8"));

        assertEquals("CFDI (SAT)", c.tipo());
        assertEquals("IVA 16%", c.nombreImpuesto());
        assertEquals(new BigDecimal("19.58"), c.impuesto());
    }

    @Test
    void pedidoDeMexicoConCedulaColombianaEsRechazado() {
        Pedido mexicanoConCedula = pedido("MX", "1020304050");

        assertThrows(IllegalArgumentException.class, () -> servicio.facturar(mexicanoConCedula));
    }

    @Test
    void paisSinFabricaEsRechazado() {
        Pedido argentino = pedido("AR", "20123456789");

        assertThrows(IllegalArgumentException.class, () -> servicio.facturar(argentino));
    }
}
