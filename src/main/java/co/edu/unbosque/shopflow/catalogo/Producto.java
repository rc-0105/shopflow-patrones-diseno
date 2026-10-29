package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;

/** Producto del catálogo. El precio está en dólares (USD). */
public record Producto(String sku, String nombre, BigDecimal precio) {
    public Producto conPrecio(BigDecimal nuevoPrecio) {
        return new Producto(sku, nombre, nuevoPrecio);
    }
}
