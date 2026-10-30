package co.edu.unbosque.shopflow.notificaciones;

import org.springframework.stereotype.Component;

/** Creador concreto (reto C): notifica por WhatsApp. Spring lo agrega solo a la List<Notificador> de PedidoService. */
@Component
public class NotificadorWhatsApp extends Notificador {

    private final BandejaNotificaciones bandeja;

    public NotificadorWhatsApp(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public String canal() {
        return NotificacionWhatsApp.CANAL;
    }

    @Override
    protected Notificacion crearNotificacion() {
        return new NotificacionWhatsApp(bandeja);
    }
}
