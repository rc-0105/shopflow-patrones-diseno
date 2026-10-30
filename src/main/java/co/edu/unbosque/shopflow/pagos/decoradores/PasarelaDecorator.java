package co.edu.unbosque.shopflow.pagos.decoradores;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.util.Objects;

// Decorador base: es una PasarelaDePago y además tiene otra adentro, a la que le pasa el cobro.
public abstract class PasarelaDecorator implements PasarelaDePago {

    protected final PasarelaDePago envuelta;

    protected PasarelaDecorator(PasarelaDePago envuelta) {
        this.envuelta = Objects.requireNonNull(envuelta, "La pasarela envuelta no puede ser nula");
    }

    @Override
    public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
        return envuelta.cobrar(montoUsd, referencia);
    }
}
