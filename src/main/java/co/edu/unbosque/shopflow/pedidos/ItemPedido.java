package co.edu.unbosque.shopflow.pedidos;

import java.math.BigDecimal;

/** Ítem de un pedido. El precio unitario viene del catálogo, nunca del cliente. */
public record ItemPedido(String sku, String nombre, int cantidad, BigDecimal precioUnitario) {

    public ItemPedido {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del producto " + sku + " debe ser mayor que cero");
        }
        if (precioUnitario == null) {
            throw new IllegalArgumentException("El producto " + sku + " no tiene precio");
        }
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
