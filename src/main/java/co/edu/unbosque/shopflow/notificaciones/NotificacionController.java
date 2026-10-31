package co.edu.unbosque.shopflow.notificaciones;

import co.edu.unbosque.shopflow.notificaciones.BandejaNotificaciones.NotificacionEnviada;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final BandejaNotificaciones bandeja;

    public NotificacionController(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @GetMapping
    public List<NotificacionEnviada> listar() {
        return bandeja.todas();
    }
}
