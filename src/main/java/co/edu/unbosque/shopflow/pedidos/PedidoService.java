package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.notificaciones.Notificador;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelasDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Dirige la construcción del pedido: traduce el JSON recibido a llamadas al builder. */
@Service
public class PedidoService {

    private final ServicioCatalogo catalogo;
    private final ServicioFacturacion facturacion;
    private final PasarelasDePago pasarelas;
    private final Map<String, Notificador> notificadores;
    private final Map<String, Pedido> pedidos = new ConcurrentHashMap<>();

    public PedidoService(ServicioCatalogo catalogo, ServicioFacturacion facturacion,
                         PasarelasDePago pasarelas, List<Notificador> notificadores) {
        this.catalogo = catalogo;
        this.facturacion = facturacion;
        this.pasarelas = pasarelas;
        this.notificadores = notificadores.stream()
                .collect(Collectors.toUnmodifiableMap(Notificador::canal, Function.identity()));
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
            // El canal es opcional; si llega, debe existir un notificador para él (400 si no).
            Optional.ofNullable(pedido.canalNotificacion()).ifPresent(this::notificadorDe);
            Pedido facturado = pedido.conComprobante(facturacion.facturar(pedido));
            pedidos.put(facturado.id(), facturado);
            return facturado;
        }
    }

    /**
     * Cobra el pedido con la pasarela del proveedor. Solo conoce la interfaz PasarelaDePago:
     * el mismo pedido se paga con LegacyPay o QuickPay sin cambiar esta clase.
     * compute() bloquea el pedido mientras se cobra, así dos pagos simultáneos no lo cobran dos veces.
     * La notificación se envía después, fuera del bloqueo, y solo si el pago fue aprobado.
     */
    public Pedido pagar(String id, String proveedor) {
        PasarelaDePago pasarela = pasarelas.obtener(proveedor);
        Pedido pagado = pedidos.compute(id, (clave, pedido) -> {
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
        notificarPagoAprobado(pagado);
        return pagado;
    }

    public Pedido buscar(String id) {
        Pedido pedido = pedidos.get(id);
        if (pedido == null) {
            throw new NoSuchElementException("No existe el pedido " + id);
        }
        return pedido;
    }

    /** Avisa al cliente por el canal que eligió, con el total pagado. Un pedido sin canal no se notifica. */
    private void notificarPagoAprobado(Pedido pagado) {
        Optional.ofNullable(pagado.canalNotificacion())
                .map(this::notificadorDe)
                .ifPresent(n -> n.notificarPagoAprobado(pagado.id(), pagado.contacto(), pagado.totalAPagar()));
    }

    private Notificador notificadorDe(String canal) {
        return Optional.ofNullable(notificadores.get(canal))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Canal de notificación no soportado: " + canal + ". Canales disponibles: "
                                + notificadores.keySet()));
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
