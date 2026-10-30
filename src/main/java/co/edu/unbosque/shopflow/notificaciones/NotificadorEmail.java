package co.edu.unbosque.shopflow.notificaciones;

import org.springframework.stereotype.Component;

/** Creador concreto: notifica por correo. */
@Component
public class NotificadorEmail extends Notificador {

    private final BandejaNotificaciones bandeja;

    public NotificadorEmail(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public String canal() {
        return NotificacionEmail.CANAL;
    }

    @Override
    protected Notificacion crearNotificacion() {
        return new NotificacionEmail(bandeja);
    }
}
