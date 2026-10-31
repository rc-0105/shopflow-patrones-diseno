package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones.NotificacionEnviada;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NotificadorWhatsAppTest {

    private final BandejaNotificaciones bandeja = new BandejaNotificaciones();
    private final NotificadorWhatsApp notificador = new NotificadorWhatsApp(bandeja);

    @Test
    void elCanalDelCreadorEsWhatsApp() {
        assertEquals("WHATSAPP", notificador.canal());
    }

    @Test
    void unPagoAprobadoQuedaEnLaBandejaComoWhatsAppConElChuloAdelante() {
        notificador.notificarPagoAprobado("PED-1", "+573001234567", new BigDecimal("145.66"));

        NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("WHATSAPP", enviada.canal());
        assertEquals("+573001234567", enviada.destino());
        assertTrue(enviada.mensaje().startsWith("✅ "));
        assertTrue(enviada.mensaje().contains("PED-1"));
        assertTrue(enviada.mensaje().contains("145.66"));
    }

    @Test
    void unNumeroSinIndicativoEsRechazado() {
        assertThrows(IllegalArgumentException.class,
                () -> notificador.notificarPagoAprobado("PED-1", "3001234567", new BigDecimal("10.00")));
        assertTrue(bandeja.todas().isEmpty());
    }

    @Test
    void unCorreoONumerosConLetrasNoSonUnNumeroValido() {
        assertThrows(IllegalArgumentException.class, () -> notificador.validarDestino("ana@correo.com"));
        assertThrows(IllegalArgumentException.class, () -> notificador.validarDestino("+57300ABC4567"));
        assertThrows(IllegalArgumentException.class, () -> notificador.validarDestino(null));
    }
}
