package co.edu.unbosque.shopflow.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PedidoBuilderTest {

    private static final ItemPedido TECLADO = new ItemPedido("SKU-001", "Teclado mecánico", 1, new BigDecimal("85.00"));
    private static final ItemPedido MOUSE = new ItemPedido("SKU-002", "Mouse inalámbrico", 2, new BigDecimal("25.50"));

    private Pedido.Builder base() {
        return new Pedido.Builder("CLI-001", "1020304050", "co", "ana.perez@correo.com").id("PED-1");
    }

    @Test
    void sinItemsLanzaIllegalArgumentException() {
        Pedido.Builder builder = base().direccionEnvio("Calle 134 # 9-51");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertTrue(e.getMessage().contains("al menos un ítem"));
    }

    @Test
    void domicilioSinDireccionLanzaIllegalArgumentException() {
        Pedido.Builder builder = base().item(TECLADO);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertTrue(e.getMessage().contains("dirección"));
    }

    @Test
    void recogerEnTiendaYExpressLanzaIllegalArgumentException() {
        Pedido.Builder builder = base().item(TECLADO).recogerEnTienda(true).envioExpress(true);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertTrue(e.getMessage().contains("express"));
    }

    @Test
    void notaDeRegaloDeMasDe140CaracteresLanzaIllegalArgumentException() {
        Pedido.Builder builder = base().item(TECLADO).recogerEnTienda(true).notaRegalo("x".repeat(141));

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void cuponBosque10AplicaDiezPorCientoDeDescuento() {
        Pedido pedido = base().item(TECLADO).item(MOUSE).direccionEnvio("Calle 134 # 9-51").cupon("bosque10").build();

        assertEquals(new BigDecimal("136.00"), pedido.subtotal());
        assertEquals(new BigDecimal("13.60"), pedido.descuento());
        assertEquals(new BigDecimal("122.40"), pedido.baseGravable());
        assertEquals("CO", pedido.pais());
        assertEquals(EstadoPedido.CREADO, pedido.estado());
    }

    @Test
    void pagarDosVecesLanzaIllegalStateException() {
        Pedido pagado = base().item(TECLADO).recogerEnTienda(true).build().pagado("LP123");

        assertEquals(EstadoPedido.PAGADO, pagado.estado());
        assertThrows(IllegalStateException.class, () -> pagado.pagado("LP456"));
    }

    @Test
    void pedidoNoTieneConstructoresPublicosNiSetters() {
        assertTrue(Arrays.stream(Pedido.class.getDeclaredConstructors())
                .noneMatch(c -> Modifier.isPublic(c.getModifiers())));
        assertTrue(Arrays.stream(Pedido.class.getDeclaredMethods())
                .noneMatch(m -> m.getName().startsWith("set")));
    }
}
