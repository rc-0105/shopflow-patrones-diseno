package co.edu.unbosque.shopflow.pagos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Guarda en memoria las entradas de auditoría de todos los cobros. */
@Component
public class RegistroAuditoria {

    private final List<EntradaAuditoria> entradas = new CopyOnWriteArrayList<>();

    public void registrar(EntradaAuditoria entrada) {
        entradas.add(entrada);
    }

    public List<EntradaAuditoria> entradas() {
        return List.copyOf(entradas);
    }
}
