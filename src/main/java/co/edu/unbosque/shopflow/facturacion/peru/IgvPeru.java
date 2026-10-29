package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import java.math.BigDecimal;
import java.math.RoundingMode;

class IgvPeru implements CalculadoraImpuestos {

    private static final BigDecimal TARIFA = new BigDecimal("0.18");

    @Override
    public String nombre() {
        return "IGV 18%";
    }

    @Override
    public BigDecimal calcular(BigDecimal base) {
        return base.multiply(TARIFA).setScale(2, RoundingMode.HALF_UP);
    }
}
