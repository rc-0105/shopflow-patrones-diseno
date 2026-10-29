package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Producto del Builder: un pedido inmutable y siempre válido.
 * Solo se crea con {@link Pedido.Builder}; los cambios de estado devuelven una copia nueva.
 */
public final class Pedido {

    public static final String CUPON_DESCUENTO = "BOSQUE10";
    private static final BigDecimal PORCENTAJE_CUPON = new BigDecimal("0.10");
    private static final int MAX_NOTA_REGALO = 140;

    private final String id;
    private final String clienteId;
    private final String documentoCliente;
    private final String pais;
    private final String contacto;
    private final List<ItemPedido> items;
    private final String direccionEnvio;
    private final boolean recogerEnTienda;
    private final String cupon;
    private final String notaRegalo;
    private final boolean envioExpress;
    private final String canalNotificacion;
    private final BigDecimal subtotal;
    private final BigDecimal descuento;
    private final EstadoPedido estado;
    private final Comprobante comprobante;
    private final String autorizacionPago;

    private Pedido(Builder b) {
        this.id = b.id;
        this.clienteId = b.clienteId;
        this.documentoCliente = b.documentoCliente;
        this.pais = b.pais;
        this.contacto = b.contacto;
        this.items = List.copyOf(b.items);
        this.direccionEnvio = b.direccionEnvio;
        this.recogerEnTienda = b.recogerEnTienda;
        this.cupon = b.cupon;
        this.notaRegalo = b.notaRegalo;
        this.envioExpress = b.envioExpress;
        this.canalNotificacion = b.canalNotificacion;
        this.subtotal = items.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.descuento = CUPON_DESCUENTO.equals(cupon)
                ? subtotal.multiply(PORCENTAJE_CUPON).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
        this.estado = EstadoPedido.CREADO;
        this.comprobante = null;
        this.autorizacionPago = null;
    }

    private Pedido(Pedido base, EstadoPedido estado, Comprobante comprobante, String autorizacionPago) {
        this.id = base.id;
        this.clienteId = base.clienteId;
        this.documentoCliente = base.documentoCliente;
        this.pais = base.pais;
        this.contacto = base.contacto;
        this.items = base.items;
        this.direccionEnvio = base.direccionEnvio;
        this.recogerEnTienda = base.recogerEnTienda;
        this.cupon = base.cupon;
        this.notaRegalo = base.notaRegalo;
        this.envioExpress = base.envioExpress;
        this.canalNotificacion = base.canalNotificacion;
        this.subtotal = base.subtotal;
        this.descuento = base.descuento;
        this.estado = estado;
        this.comprobante = comprobante;
        this.autorizacionPago = autorizacionPago;
    }

    /** Copia del pedido con su comprobante de facturación. */
    public Pedido conComprobante(Comprobante comprobante) {
        return new Pedido(this, estado, Objects.requireNonNull(comprobante), autorizacionPago);
    }

    /** Copia del pedido en estado PAGADO. Un pedido ya pagado no se puede volver a pagar (409). */
    public Pedido pagado(String autorizacion) {
        if (estado == EstadoPedido.PAGADO) {
            throw new IllegalStateException("El pedido " + id + " ya fue pagado");
        }
        return new Pedido(this, EstadoPedido.PAGADO, comprobante, autorizacion);
    }

    /** Subtotal menos descuento: la base sobre la que se calcula el impuesto. */
    public BigDecimal baseGravable() {
        return subtotal.subtract(descuento);
    }

    /** Monto a cobrar: el total del comprobante si ya fue facturado. */
    public BigDecimal totalAPagar() {
        return comprobante != null ? comprobante.total() : baseGravable();
    }

    public String id() { return id; }
    public String clienteId() { return clienteId; }
    public String documentoCliente() { return documentoCliente; }
    public String pais() { return pais; }
    public String contacto() { return contacto; }
    public List<ItemPedido> items() { return items; }
    public String direccionEnvio() { return direccionEnvio; }
    public boolean recogerEnTienda() { return recogerEnTienda; }
    public String cupon() { return cupon; }
    public String notaRegalo() { return notaRegalo; }
    public boolean envioExpress() { return envioExpress; }
    public String canalNotificacion() { return canalNotificacion; }
    public BigDecimal subtotal() { return subtotal; }
    public BigDecimal descuento() { return descuento; }
    public EstadoPedido estado() { return estado; }
    public Comprobante comprobante() { return comprobante; }
    public String autorizacionPago() { return autorizacionPago; }

    /**
     * Builder de Bloch: los datos obligatorios van en el constructor, cada opción
     * devuelve el propio builder y build() valida todas las reglas en un solo lugar.
     * El país, el documento y el canal no se validan aquí: los validan la fábrica
     * de facturación y el notificador correspondientes (así Perú o WhatsApp no tocan esta clase).
     */
    public static final class Builder {

        private final String clienteId;
        private final String documentoCliente;
        private final String pais;
        private final String contacto;
        private final List<ItemPedido> items = new ArrayList<>();
        private String id;
        private String direccionEnvio;
        private boolean recogerEnTienda;
        private String cupon;
        private String notaRegalo;
        private boolean envioExpress;
        private String canalNotificacion;

        public Builder(String clienteId, String documentoCliente, String pais, String contacto) {
            this.clienteId = limpiar(clienteId);
            this.documentoCliente = limpiar(documentoCliente);
            this.pais = mayusculas(pais);
            this.contacto = limpiar(contacto);
        }

        public Builder id(String id) {
            this.id = limpiar(id);
            return this;
        }

        public Builder item(ItemPedido item) {
            items.add(Objects.requireNonNull(item, "El ítem no puede ser nulo"));
            return this;
        }

        public Builder direccionEnvio(String direccionEnvio) {
            this.direccionEnvio = limpiar(direccionEnvio);
            return this;
        }

        public Builder recogerEnTienda(boolean recogerEnTienda) {
            this.recogerEnTienda = recogerEnTienda;
            return this;
        }

        public Builder cupon(String cupon) {
            this.cupon = mayusculas(cupon);
            return this;
        }

        public Builder notaRegalo(String notaRegalo) {
            this.notaRegalo = limpiar(notaRegalo);
            return this;
        }

        public Builder envioExpress(boolean envioExpress) {
            this.envioExpress = envioExpress;
            return this;
        }

        public Builder canalNotificacion(String canalNotificacion) {
            this.canalNotificacion = mayusculas(canalNotificacion);
            return this;
        }

        public Pedido build() {
            requerido(id, "El pedido necesita un id");
            requerido(clienteId, "El clienteId es obligatorio");
            requerido(documentoCliente, "El documento del cliente es obligatorio");
            requerido(pais, "El país es obligatorio");
            requerido(contacto, "El contacto es obligatorio");
            if (items.isEmpty()) {
                throw new IllegalArgumentException("El pedido debe tener al menos un ítem");
            }
            if (!recogerEnTienda && direccionEnvio == null) {
                throw new IllegalArgumentException("La dirección de envío es obligatoria si el pedido no se recoge en tienda");
            }
            if (recogerEnTienda && envioExpress) {
                throw new IllegalArgumentException("Un pedido para recoger en tienda no puede tener envío express");
            }
            if (notaRegalo != null && notaRegalo.length() > MAX_NOTA_REGALO) {
                throw new IllegalArgumentException("La nota de regalo admite máximo " + MAX_NOTA_REGALO + " caracteres");
            }
            if (cupon != null && !CUPON_DESCUENTO.equals(cupon)) {
                throw new IllegalArgumentException("El cupón " + cupon + " no es válido");
            }
            return new Pedido(this);
        }

        private static void requerido(String valor, String mensaje) {
            if (valor == null) {
                throw new IllegalArgumentException(mensaje);
            }
        }

        private static String limpiar(String valor) {
            return valor == null || valor.isBlank() ? null : valor.trim();
        }

        private static String mayusculas(String valor) {
            String limpio = limpiar(valor);
            return limpio == null ? null : limpio.toUpperCase();
        }
    }
}
