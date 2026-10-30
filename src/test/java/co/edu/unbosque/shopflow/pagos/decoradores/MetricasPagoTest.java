package co.edu.unbosque.shopflow.pagos.decoradores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MetricasPagoTest {

    @Test
    void cuentaCobrosYAprobadosYCalculaLaTasa() {
        int[] llamadas = {0};
        PasarelaDePago apruebaUnoDeCada4 = (monto, ref) ->
                ++llamadas[0] % 4 == 0 ? ResultadoPago.aprobado("AUT") : ResultadoPago.rechazado("Fondos insuficientes");
        MetricasPago metricas = new MetricasPago(apruebaUnoDeCada4, "LEGACYPAY");

        for (int i = 0; i < 4; i++) {
            metricas.cobrar(BigDecimal.TEN, "PED-" + i);
        }

        assertEquals(4, metricas.cobros());
        assertEquals(1, metricas.aprobados());
        assertEquals(25.0, metricas.tasaDeAprobacion());
    }

    @Test
    void unCobroQueFallaCuentaComoNoAprobadoYPropagaLaExcepcion() {
        PasarelaDePago caida = (monto, ref) -> { throw new PasarelaNoDisponibleException("caída", null); };
        MetricasPago metricas = new MetricasPago(caida, "LEGACYPAY");

        assertThrows(PasarelaNoDisponibleException.class, () -> metricas.cobrar(BigDecimal.TEN, "PED-1"));
        assertEquals(1, metricas.cobros());
        assertEquals(0, metricas.aprobados());
    }
}
