package co.edu.unbosque.shopflow.notificaciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Simula el envío real: guarda en memoria cada notificación enviada. Código base. */
@Component
public class BandejaNotificaciones {

    public record NotificacionEnviada(String canal, String destino, String mensaje, LocalDateTime fecha) { }

    private final List<NotificacionEnviada> enviadas = new CopyOnWriteArrayList<>();

    public void registrar(String canal, String destino, String mensaje) {
        enviadas.add(new NotificacionEnviada(canal, destino, mensaje, LocalDateTime.now()));
    }

    public List<NotificacionEnviada> todas() {
        return List.copyOf(enviadas);
    }
}
