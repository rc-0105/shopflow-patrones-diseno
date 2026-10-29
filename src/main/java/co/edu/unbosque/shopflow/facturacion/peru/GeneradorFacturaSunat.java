package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

class GeneradorFacturaSunat implements GeneradorComprobante {

    private final AtomicInteger consecutivo;

    GeneradorFacturaSunat(AtomicInteger consecutivo) {
        this.consecutivo = consecutivo;
    }

    @Override
    public Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto) {
        return new Comprobante("Factura electrónica (SUNAT)", "FE-PE-" + consecutivo.incrementAndGet(),
                base, nombreImpuesto, impuesto, base.add(impuesto));
    }
}
