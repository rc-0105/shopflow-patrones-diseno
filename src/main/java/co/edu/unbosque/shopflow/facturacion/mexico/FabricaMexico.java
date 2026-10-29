package co.edu.unbosque.shopflow.facturacion.mexico;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import co.edu.unbosque.shopflow.facturacion.FabricaFacturacion;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/** Fábrica concreta de México: IVA 16 %, RFC y comprobante CFDI del SAT. */
@Component
public class FabricaMexico implements FabricaFacturacion {

    private final AtomicInteger consecutivo = new AtomicInteger();

    @Override
    public String pais() {
        return "MX";
    }

    @Override
    public CalculadoraImpuestos crearCalculadora() {
        return new IvaMexico();
    }

    @Override
    public ValidadorIdentificacion crearValidador() {
        return new ValidadorRfc();
    }

    @Override
    public GeneradorComprobante crearGenerador() {
        return new GeneradorCfdi(consecutivo);
    }
}
