package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelasDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** Dirige la construcción del pedido: traduce el JSON recibido a llamadas al builder. */
@Service
public class PedidoService {

    private final ServicioCatalogo catalogo;
    private final ServicioFacturacion facturacion;
    private final PasarelasDePago pasarelas;
    private final Map<String, Pedido> pedidos = new ConcurrentHashMap<>();

    public PedidoService(ServicioCatalogo catalogo, ServicioFacturacion facturacion, PasarelasDePago pasarelas) {
        this.catalogo = catalogo;
        this.facturacion = facturacion;
        this.pasarelas = pasarelas;
    }

    public Pedido crear(PedidoRequest request) {
        List<ItemPedido> items = itemsDelCatalogo(request.items());
        synchronized (this) {
            Pedido.Builder builder = new Pedido.Builder(
                    request.clienteId(), request.documentoCliente(), request.pais(), request.contacto())
                    .id("PED-" + (pedidos.size() + 1))
                    .direccionEnvio(request.direccionEnvio())
                    .recogerEnTienda(Boolean.TRUE.equals(request.recogerEnTienda()))
                    .cupon(request.cupon())
                    .notaRegalo(request.notaRegalo())
                    .envioExpress(Boolean.TRUE.equals(request.envioExpress()))
                    .canalNotificacion(request.canalNotificacion());
            items.forEach(builder::item);

            Pedido pedido = builder.build();
            Pedido facturado = pedido.conComprobante(facturacion.facturar(pedido));
            pedidos.put(facturado.id(), facturado);
            return facturado;
        }
    }

    /**
     * Cobra el pedido con la pasarela del proveedor. Solo conoce la interfaz PasarelaDePago:
     * el mismo pedido se paga con LegacyPay o QuickPay sin cambiar esta clase.
     * compute() bloquea el pedido mientras se cobra, así dos pagos simultáneos no lo cobran dos veces.
     */
    public Pedido pagar(String id, String proveedor) {
        PasarelaDePago pasarela = pasarelas.obtener(proveedor);
        return pedidos.compute(id, (clave, pedido) -> {
            if (pedido == null) {
                throw new NoSuchElementException("No existe el pedido " + id);
            }
            if (pedido.estado() == EstadoPedido.PAGADO) {
                throw new IllegalStateException("El pedido " + id + " ya fue pagado");
            }
            ResultadoPago resultado = pasarela.cobrar(pedido.totalAPagar(), pedido.id());
            if (!resultado.aprobado()) {
                throw new IllegalStateException(resultado.mensaje());
            }
            return pedido.pagado(resultado.autorizacion());
        });
    }

    public Pedido buscar(String id) {
        Pedido pedido = pedidos.get(id);
        if (pedido == null) {
            throw new NoSuchElementException("No existe el pedido " + id);
        }
        return pedido;
    }

    private List<ItemPedido> itemsDelCatalogo(List<PedidoRequest.ItemRequest> solicitados) {
        if (solicitados == null) {
            return List.of();
        }
        return solicitados.stream()
                .map(item -> {
                    Producto producto = catalogo.buscar(item.sku());
                    return new ItemPedido(producto.sku(), producto.nombre(), item.cantidad(), producto.precio());
                })
                .toList();
    }
}
