package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Producto abstracto de la familia: el impuesto del país. */
public interface CalculadoraImpuestos {
    String nombre();
    BigDecimal calcular(BigDecimal base);
}
