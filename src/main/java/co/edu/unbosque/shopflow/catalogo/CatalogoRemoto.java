package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simula el servicio de catálogo de otro equipo, desplegado en otra máquina:
 * cada llamada tarda 1,5 segundos. Código base: NO SE MODIFICA.
 */
public class CatalogoRemoto implements ServicioCatalogo {

    private final Map<String, Producto> productos = new ConcurrentHashMap<>(Map.of(
            "SKU-001", new Producto("SKU-001", "Teclado mecánico", new BigDecimal("85.00")),
            "SKU-002", new Producto("SKU-002", "Mouse inalámbrico", new BigDecimal("25.50")),
            "SKU-003", new Producto("SKU-003", "Monitor 27 pulgadas", new BigDecimal("320.00")),
            "SKU-004", new Producto("SKU-004", "Portátil 14 pulgadas", new BigDecimal("1450.00")),
            "SKU-005", new Producto("SKU-005", "Diadema con micrófono", new BigDecimal("60.00"))));

    private int llamadas = 0;

    @Override
    public Producto buscar(String sku) {
        latenciaDeRed();
        Producto p = productos.get(sku);
        if (p == null) {
            throw new NoSuchElementException("No existe el producto " + sku);
        }
        return p;
    }

    @Override
    public List<Producto> listar() {
        latenciaDeRed();
        return new ArrayList<>(productos.values());
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        latenciaDeRed();
        Producto actualizado = buscarSinLatencia(sku).conPrecio(nuevoPrecio);
        productos.put(sku, actualizado);
        return actualizado;
    }

    /** Número de llamadas que llegaron al servicio remoto (útil para probar el proxy). */
    public synchronized int llamadasRecibidas() {
        return llamadas;
    }

    private Producto buscarSinLatencia(String sku) {
        Producto p = productos.get(sku);
        if (p == null) {
            throw new NoSuchElementException("No existe el producto " + sku);
        }
        return p;
    }

    private void latenciaDeRed() {
        synchronized (this) { llamadas++; }
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
