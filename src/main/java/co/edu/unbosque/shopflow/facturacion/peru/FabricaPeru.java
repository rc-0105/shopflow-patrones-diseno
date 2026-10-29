package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import co.edu.unbosque.shopflow.facturacion.FabricaFacturacion;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/** Reto A: fábrica concreta de Perú (IGV 18 %, RUC y factura electrónica de la SUNAT). Solo archivos nuevos. */
@Component
public class FabricaPeru implements FabricaFacturacion {

    private final AtomicInteger consecutivo = new AtomicInteger();

    @Override
    public String pais() {
        return "PE";
    }

    @Override
    public CalculadoraImpuestos crearCalculadora() {
        return new IgvPeru();
    }

    @Override
    public ValidadorIdentificacion crearValidador() {
        return new ValidadorRuc();
    }

    @Override
    public GeneradorComprobante crearGenerador() {
        return new GeneradorFacturaSunat(consecutivo);
    }
}
