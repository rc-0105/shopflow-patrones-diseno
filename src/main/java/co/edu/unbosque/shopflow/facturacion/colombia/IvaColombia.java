package co.edu.unbosque.shopflow.facturacion.colombia;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import java.math.BigDecimal;
import java.math.RoundingMode;

class IvaColombia implements CalculadoraImpuestos {

    private static final BigDecimal TARIFA = new BigDecimal("0.19");

    @Override
    public String nombre() {
        return "IVA 19%";
    }

    @Override
    public BigDecimal calcular(BigDecimal base) {
        return base.multiply(TARIFA).setScale(2, RoundingMode.HALF_UP);
    }
}
