package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy de caché: misma interfaz que el catálogo real, pero solo le delega
 * cuando el producto todavía no está en memoria.
 */
public class CatalogoCacheProxy implements ServicioCatalogo {

    private final ServicioCatalogo real;
    private final Map<String, Producto> cache = new ConcurrentHashMap<>();
    private volatile boolean catalogoCompleto = false;

    public CatalogoCacheProxy(ServicioCatalogo real) {
        this.real = real;
    }

    @Override
    public Producto buscar(String sku) {
        // Si el real lanza NoSuchElementException, no se guarda nada y la excepción sigue (404).
        return cache.computeIfAbsent(sku, real::buscar);
    }

    @Override
    public List<Producto> listar() {
        if (!catalogoCompleto) {
            real.listar().forEach(p -> cache.put(p.sku(), p));
            catalogoCompleto = true;
        }
        return new ArrayList<>(cache.values());
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        Producto actualizado = real.actualizarPrecio(sku, nuevoPrecio);
        cache.put(sku, actualizado);
        return actualizado;
    }
}
