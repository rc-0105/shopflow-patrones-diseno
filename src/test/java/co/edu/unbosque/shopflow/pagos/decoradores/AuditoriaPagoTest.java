package co.edu.unbosque.shopflow.pagos.decoradores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.pagos.EntradaAuditoria;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.RegistroAuditoria;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AuditoriaPagoTest {

    private final RegistroAuditoria registro = new RegistroAuditoria();

    private PasarelaDePago fallaDosVecesYAprueba() {
        int[] llamadas = {0};
        return (monto, ref) -> {
            if (++llamadas[0] < 3) throw new PasarelaNoDisponibleException("caída", null);
            return ResultadoPago.aprobado("AUT-1");
        };
    }

    @Test
    void conLaAuditoriaPorFueraDelReintentoHayUnaEntradaPorCobro() {
        PasarelaDePago pasarela = new AuditoriaPago(
                new ReintentoPago(fallaDosVecesYAprueba(), 3), "LEGACYPAY", registro);

        pasarela.cobrar(new BigDecimal("50.00"), "PED-1");

        assertEquals(1, registro.entradas().size());
        EntradaAuditoria entrada = registro.entradas().get(0);
        assertEquals("LEGACYPAY", entrada.proveedor());
        assertEquals("PED-1", entrada.referencia());
        assertEquals(new BigDecimal("50.00"), entrada.montoUsd());
        assertEquals("APROBADO", entrada.resultado());
    }

    @Test
    void conLaAuditoriaPorDentroDelReintentoHayUnaEntradaPorIntento() {
        PasarelaDePago pasarela = new ReintentoPago(
                new AuditoriaPago(fallaDosVecesYAprueba(), "LEGACYPAY", registro), 3);

        pasarela.cobrar(new BigDecimal("50.00"), "PED-1");

        assertEquals(3, registro.entradas().size());
    }

    @Test
    void registraElCobroFallidoYDejaPasarLaExcepcion() {
        PasarelaDePago caida = (monto, ref) -> { throw new PasarelaNoDisponibleException("caída", null); };

        assertThrows(PasarelaNoDisponibleException.class,
                () -> new AuditoriaPago(caida, "LEGACYPAY", registro).cobrar(BigDecimal.TEN, "PED-1"));
        assertEquals("ERROR: caída", registro.entradas().get(0).resultado());
    }
}
