package co.edu.unbosque.shopflow.facturacion.colombia;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import co.edu.unbosque.shopflow.facturacion.FabricaFacturacion;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/** Fábrica concreta de Colombia: IVA 19 %, cédula o NIT y factura electrónica de la DIAN. */
@Component
public class FabricaColombia implements FabricaFacturacion {

    private final AtomicInteger consecutivo = new AtomicInteger();

    @Override
    public String pais() {
        return "CO";
    }

    @Override
    public CalculadoraImpuestos crearCalculadora() {
        return new IvaColombia();
    }

    @Override
    public ValidadorIdentificacion crearValidador() {
        return new ValidadorCedulaNit();
    }

    @Override
    public GeneradorComprobante crearGenerador() {
        return new GeneradorFacturaDian(consecutivo);
    }
}
