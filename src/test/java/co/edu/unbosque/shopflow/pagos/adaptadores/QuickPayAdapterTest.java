package co.edu.unbosque.shopflow.pagos.adaptadores;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class QuickPayAdapterTest {

    private final QuickPayAdapter adapter = new QuickPayAdapter();

    @Test
    void okSeTraduceAAprobado() {
        ResultadoPago r = adapter.cobrar(new BigDecimal("3451.00"), "PED-2");

        assertTrue(r.aprobado());
        assertNotNull(r.autorizacion());
    }

    @Test
    void rejectedSeTraduceARechazado() {
        ResultadoPago r = adapter.cobrar(new BigDecimal("5000.01"), "PED-2");

        assertFalse(r.aprobado());
    }

    @Test
    void errorSeTraduceARechazado() {
        ResultadoPago r = adapter.cobrar(BigDecimal.ZERO, "PED-2");

        assertFalse(r.aprobado());
    }
}
