package co.edu.unbosque.shopflow.pagos.adaptadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayClient;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LegacyPayAdapterTest {

    @Test
    void elTimeoutDelSdkSeTraduceAPasarelaNoDisponible() {
        LegacyPayAdapter adapter = new LegacyPayAdapter(new LegacyPayClient(1.0));

        assertThrows(PasarelaNoDisponibleException.class,
                () -> adapter.cobrar(new BigDecimal("100.00"), "PED-1"));
    }

    @Test
    void unCobroValidoSeApruebaConLaAutorizacionDelProveedor() {
        ResultadoPago r = new LegacyPayAdapter(new LegacyPayClient(0.0)).cobrar(new BigDecimal("145.66"), "PED-1");

        assertTrue(r.aprobado());
        assertTrue(r.autorizacion().startsWith("LP"));
    }

    @Test
    void masDeDosMilDolaresSeRechazaPorFondosInsuficientes() {
        ResultadoPago r = new LegacyPayAdapter(new LegacyPayClient(0.0)).cobrar(new BigDecimal("2000.01"), "PED-1");

        assertFalse(r.aprobado());
        assertEquals("Fondos insuficientes", r.mensaje());
    }

    @Test
    void exactamenteDosMilDolaresSeAprueba() {
        ResultadoPago r = new LegacyPayAdapter(new LegacyPayClient(0.0)).cobrar(new BigDecimal("2000.00"), "PED-1");

        assertTrue(r.aprobado());
    }
}
