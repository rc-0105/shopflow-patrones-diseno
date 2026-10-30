package co.edu.unbosque.shopflow.pedidos;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servicio;

    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@RequestBody PedidoRequest request) {
        Pedido pedido = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + pedido.id()))
                .body(PedidoResponse.de(pedido));
    }

    @PostMapping("/{id}/pago")
    public PedidoResponse pagar(@PathVariable String id, @RequestBody PagoRequest request) {
        return PedidoResponse.de(servicio.pagar(id, request.proveedor()));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable String id) {
        return PedidoResponse.de(servicio.buscar(id));
    }
}
