package co.edu.unbosque.shopflow.pagos;

/** Error propio de ShopFlow cuando la pasarela no responde. */
public class PasarelaNoDisponibleException extends RuntimeException {
    public PasarelaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
