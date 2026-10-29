package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Comprobante electrónico que se incluye en la respuesta de POST /api/pedidos. */
public record Comprobante(String tipo, String numero, BigDecimal base, String nombreImpuesto,
                          BigDecimal impuesto, BigDecimal total) { }
