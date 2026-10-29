package co.edu.unbosque.shopflow.pedidos;

import java.util.List;

/** Cuerpo JSON de POST /api/pedidos. Varios campos son opcionales (pueden llegar en null). */
public record PedidoRequest(
        String clienteId,
        String documentoCliente,
        String pais,
        String contacto,
        List<ItemRequest> items,
        String direccionEnvio,
        Boolean recogerEnTienda,
        String cupon,
        String notaRegalo,
        Boolean envioExpress,
        String canalNotificacion) {

    public record ItemRequest(String sku, int cantidad) { }
}
