package co.edu.unbosque.shopflow.pagos;

import java.math.BigDecimal;
import java.time.Instant;

/** Un cobro auditado: proveedor, monto, resultado y duración. */
public record EntradaAuditoria(
        Instant fecha,
        String proveedor,
        String referencia,
        BigDecimal montoUsd,
        String resultado,
        long duracionMs) { }
