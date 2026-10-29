package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Producto abstracto de la familia: el comprobante electrónico del país. */
public interface GeneradorComprobante {
    Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto);
}
