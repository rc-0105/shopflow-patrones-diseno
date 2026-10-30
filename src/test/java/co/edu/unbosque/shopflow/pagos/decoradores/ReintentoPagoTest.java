package co.edu.unbosque.shopflow.pagos.decoradores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ReintentoPagoTest {

    @Test
    void reintentaHastaQueLaPasarelaResponde() {
        int[] llamadas = {0};
        PasarelaDePago inestable = (monto, ref) -> {
            if (++llamadas[0] < 3) throw new PasarelaNoDisponibleException("caída", null);
            return ResultadoPago.aprobado("AUT-1");
        };

        ResultadoPago r = new ReintentoPago(inestable, 3).cobrar(BigDecimal.TEN, "PED-1");

        assertTrue(r.aprobado());
        assertEquals(3, llamadas[0]);
    }

    @Test
    void siSeAgotanLosIntentosPropagaLaExcepcion() {
        int[] llamadas = {0};
        PasarelaDePago caida = (monto, ref) -> {
            llamadas[0]++;
            throw new PasarelaNoDisponibleException("caída", null);
        };

        assertThrows(PasarelaNoDisponibleException.class,
                () -> new ReintentoPago(caida, 3).cobrar(BigDecimal.TEN, "PED-1"));
        assertEquals(3, llamadas[0]);
    }

    @Test
    void unRechazoNoSeReintenta() {
        int[] llamadas = {0};
        PasarelaDePago sinFondos = (monto, ref) -> {
            llamadas[0]++;
            return ResultadoPago.rechazado("Fondos insuficientes");
        };

        ResultadoPago r = new ReintentoPago(sinFondos, 3).cobrar(BigDecimal.TEN, "PED-1");

        assertFalse(r.aprobado());
        assertEquals(1, llamadas[0]);
    }
}
