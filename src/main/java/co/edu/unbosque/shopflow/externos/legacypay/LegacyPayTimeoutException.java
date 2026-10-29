package co.edu.unbosque.shopflow.externos.legacypay;

/** Excepción propia del SDK de LegacyPay. NO SE MODIFICA. */
public class LegacyPayTimeoutException extends RuntimeException {
    public LegacyPayTimeoutException(String message) {
        super(message);
    }
}
