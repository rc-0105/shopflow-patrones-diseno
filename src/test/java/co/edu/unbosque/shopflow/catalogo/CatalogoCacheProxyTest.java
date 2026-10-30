package co.edu.unbosque.shopflow.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.comun.AccesoDenegadoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class CatalogoCacheProxyTest {

    /** Catálogo falso escrito a mano: no tiene latencia y cuenta cada llamada que recibe. */
    static class CatalogoContador implements ServicioCatalogo {
        final Map<String, Producto> productos = new HashMap<>(Map.of(
                "SKU-001", new Producto("SKU-001", "Teclado mecánico", new BigDecimal("85.00")),
                "SKU-003", new Producto("SKU-003", "Monitor 27 pulgadas", new BigDecimal("320.00"))));
        int llamadas = 0;

        @Override
        public Producto buscar(String sku) {
            llamadas++;
            Producto p = productos.get(sku);
            if (p == null) {
                throw new NoSuchElementException("No existe el producto " + sku);
            }
            return p;
        }

        @Override
        public List<Producto> listar() {
            llamadas++;
            return new ArrayList<>(productos.values());
        }

        @Override
        public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
            llamadas++;
            Producto actualizado = productos.get(sku).conPrecio(nuevoPrecio);
            productos.put(sku, actualizado);
            return actualizado;
        }
    }

    private final CatalogoContador real = new CatalogoContador();
    private final CatalogoCacheProxy proxy = new CatalogoCacheProxy(real);

    @Test
    void dosBusquedasDelMismoSkuLleganUnaSolaVezAlCatalogoReal() {
        proxy.buscar("SKU-001");
        Producto segunda = proxy.buscar("SKU-001");

        assertEquals(1, real.llamadas);
        assertEquals(new BigDecimal("85.00"), segunda.precio());
    }

    @Test
    void despuesDeListarLasBusquedasSalenDeLaCache() {
        assertEquals(2, proxy.listar().size());
        proxy.listar();
        proxy.buscar("SKU-003");

        assertEquals(1, real.llamadas);
    }

    @Test
    void actualizarPrecioRefrescaLaCacheSinVolverAConsultar() {
        proxy.buscar("SKU-003");
        proxy.actualizarPrecio("SKU-003", new BigDecimal("299.90"));

        assertEquals(new BigDecimal("299.90"), proxy.buscar("SKU-003").precio());
        assertEquals(2, real.llamadas);
    }

    @Test
    void skuInexistenteNoQuedaEnCache() {
        assertThrows(NoSuchElementException.class, () -> proxy.buscar("SKU-999"));
        assertThrows(NoSuchElementException.class, () -> proxy.buscar("SKU-999"));

        assertEquals(2, real.llamadas);
    }

    @Test
    void clienteNoPuedeCambiarPreciosYNoLlegaAlCatalogo() {
        ServicioCatalogo protegido = new CatalogoProteccionProxy(real, "CLIENTE");

        assertThrows(AccesoDenegadoException.class,
                () -> protegido.actualizarPrecio("SKU-003", new BigDecimal("1.00")));
        assertEquals(0, real.llamadas);
        assertEquals(new BigDecimal("320.00"), real.productos.get("SKU-003").precio());
    }

    @Test
    void sinRolTampocoSePuedenCambiarPrecios() {
        ServicioCatalogo protegido = new CatalogoProteccionProxy(real, null);

        assertThrows(AccesoDenegadoException.class,
                () -> protegido.actualizarPrecio("SKU-003", new BigDecimal("1.00")));
    }

    @Test
    void adminCambiaPreciosYCualquierRolPuedeConsultar() {
        Producto actualizado = new CatalogoProteccionProxy(real, "admin")
                .actualizarPrecio("SKU-003", new BigDecimal("299.90"));
        Producto consultado = new CatalogoProteccionProxy(real, "CLIENTE").buscar("SKU-003");

        assertEquals(new BigDecimal("299.90"), actualizado.precio());
        assertEquals(new BigDecimal("299.90"), consultado.precio());
    }
}
