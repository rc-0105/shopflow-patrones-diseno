package co.edu.unbosque.shopflow.facturacion.peru;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.facturacion.colombia.FabricaColombia;
import co.edu.unbosque.shopflow.facturacion.mexico.FabricaMexico;
import co.edu.unbosque.shopflow.pedidos.ItemPedido;
import co.edu.unbosque.shopflow.pedidos.Pedido;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class FabricaPeruTest {

    private final ServicioFacturacion servicio =
            new ServicioFacturacion(List.of(new FabricaColombia(), new FabricaMexico(), new FabricaPeru()));

    private Pedido pedidoPeru(String documento) {
        return new Pedido.Builder("CLI-002", documento, "PE", "+51987654321")
                .id("PED-1")
                .item(new ItemPedido("SKU-001", "Teclado mecánico", 1, new BigDecimal("85.00")))
                .item(new ItemPedido("SKU-002", "Mouse inalámbrico", 2, new BigDecimal("25.50")))
                .direccionEnvio("Av. Larco 345, Miraflores")
                .cupon("BOSQUE10")
                .build();
    }

    @Test
    void pedidoDePeruGeneraFacturaSunatConIgv18() {
        Comprobante c = servicio.facturar(pedidoPeru("20123456789"));

        assertEquals("Factura electrónica (SUNAT)", c.tipo());
        assertEquals("FE-PE-1", c.numero());
        assertEquals("IGV 18%", c.nombreImpuesto());
        assertEquals(new BigDecimal("22.03"), c.impuesto());
        assertEquals(new BigDecimal("144.43"), c.total());
    }

    @Test
    void rucQueNoTiene11DigitosEsRechazado() {
        Pedido conCedula = pedidoPeru("1020304050");

        assertThrows(IllegalArgumentException.class, () -> servicio.facturar(conCedula));
    }
}
