package co.edu.unbosque.shopflow.pagos;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditoriaController {

    private final RegistroAuditoria registro;

    public AuditoriaController(RegistroAuditoria registro) {
        this.registro = registro;
    }

    @GetMapping("/api/pagos/auditoria")
    public List<EntradaAuditoria> auditoria() {
        return registro.entradas();
    }
}
