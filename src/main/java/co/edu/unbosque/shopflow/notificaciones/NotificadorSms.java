package co.edu.unbosque.shopflow.notificaciones;

import org.springframework.stereotype.Component;

/** Creador concreto: notifica por SMS. */
@Component
public class NotificadorSms extends Notificador {

    private final BandejaNotificaciones bandeja;

    public NotificadorSms(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public String canal() {
        return NotificacionSms.CANAL;
    }

    @Override
    protected Notificacion crearNotificacion() {
        return new NotificacionSms(bandeja);
    }
}
