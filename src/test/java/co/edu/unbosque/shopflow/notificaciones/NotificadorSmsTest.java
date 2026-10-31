package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones.NotificacionEnviada;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NotificadorSmsTest {

    private final BandejaNotificaciones bandeja = new BandejaNotificaciones();
    private final NotificadorSms notificador = new NotificadorSms(bandeja);

    @Test
    void unPagoAprobadoQuedaEnLaBandejaComoSmsConElTotal() {
        notificador.notificarPagoAprobado("PED-2", "3001234567", new BigDecimal("80.00"));

        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("SMS", enviada.canal());
        assertEquals("3001234567", enviada.destino());
        assertTrue(enviada.mensaje().contains("80.00"));
        assertTrue(enviada.mensaje().length() <= 160);
    }

    @Test
    void elSmsSeRecortaA160Caracteres() {
        new NotificacionSms(bandeja).enviar("3001234567", "x".repeat(200));

        assertEquals(160, bandeja.todas().get(0).mensaje().length());
    }
}
