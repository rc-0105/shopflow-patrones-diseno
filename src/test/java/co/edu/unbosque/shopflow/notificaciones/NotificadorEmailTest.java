package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones.NotificacionEnviada;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NotificadorEmailTest {

    private final BandejaNotificaciones bandeja = new BandejaNotificaciones();
    private final NotificadorEmail notificador = new NotificadorEmail(bandeja);

    @Test
    void unContactoSinArrobaEsRechazado() {
        assertThrows(IllegalArgumentException.class,
                () -> notificador.notificarPagoAprobado("PED-1", "3001234567", new BigDecimal("145.66")));
        assertTrue(bandeja.todas().isEmpty());
    }

    @Test
    void unPagoAprobadoQuedaEnLaBandejaComoCorreoConAsuntoYTotal() {
        notificador.notificarPagoAprobado("PED-1", "ana.perez@correo.com", new BigDecimal("145.66"));

        assertEquals(1, bandeja.todas().size());
        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("EMAIL", enviada.canal());
        assertEquals("ana.perez@correo.com", enviada.destino());
        assertTrue(enviada.mensaje().startsWith("[Pago aprobado]"));
        assertTrue(enviada.mensaje().contains("PED-1"));
        assertTrue(enviada.mensaje().contains("145.66"));
    }

    @Test
    void elCanalDelCreadorEsEmail() {
        assertEquals("EMAIL", notificador.canal());
    }
}
