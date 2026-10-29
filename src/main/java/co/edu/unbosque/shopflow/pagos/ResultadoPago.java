package co.edu.unbosque.shopflow.pagos;

/** Resultado de un cobro, en el lenguaje de ShopFlow. */
public record ResultadoPago(boolean aprobado, String autorizacion, String mensaje) {
    public static ResultadoPago aprobado(String autorizacion) {
        return new ResultadoPago(true, autorizacion, "Pago aprobado");
    }
    public static ResultadoPago rechazado(String mensaje) {
        return new ResultadoPago(false, null, mensaje);
    }
}
