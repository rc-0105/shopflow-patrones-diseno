package co.edu.unbosque.shopflow.pagos.decoradores;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;

// Decorador: si la pasarela no responde, vuelve a intentar el cobro.
public class ReintentoPago extends PasarelaDecorator {

    private final int maxIntentos;

    public ReintentoPago(PasarelaDePago envuelta, int maxIntentos) {
        super(envuelta);
        if (maxIntentos < 1) {
            throw new IllegalArgumentException("max-intentos debe ser al menos 1");
        }
        this.maxIntentos = maxIntentos;
    }

    @Override
    public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
        PasarelaNoDisponibleException ultimoError = null;
        for (int intento = 1; intento <= maxIntentos; intento++) {
            try {
                return super.cobrar(montoUsd, referencia);
            } catch (PasarelaNoDisponibleException e) {
                // Solo se reintenta cuando la pasarela está caída; un rechazo no se reintenta.
                ultimoError = e;
            }
        }
        throw ultimoError;
    }
}
