package co.edu.unbosque.shopflow.externos.legacypay;

import java.util.Random;

/**
 * SDK del proveedor LegacyPay (simulado). Código de un tercero: NO SE MODIFICA.
 * Trabaja en centavos de dólar y responde con un texto "CODIGO|AUTORIZACION":
 * 00 = aprobado, 51 = fondos insuficientes, 05 = transacción no permitida.
 * Falla de forma intermitente con LegacyPayTimeoutException.
 */
public class LegacyPayClient {

    private final double timeoutProbability;
    private final Random random = new Random();

    public LegacyPayClient(double timeoutProbability) {
        this.timeoutProbability = timeoutProbability;
    }

    public String makeCharge(long amountInCents, String currency, String reference) {
        sleep(300);
        if (random.nextDouble() < timeoutProbability) {
            throw new LegacyPayTimeoutException("LegacyPay gateway timeout, ref=" + reference);
        }
        if (!"USD".equals(currency)) {
            return "05|";
        }
        if (amountInCents > 200_000L) {          // más de 2.000 USD
            return "51|";
        }
        return "00|LP" + Math.abs(reference.hashCode() % 1_000_000);
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
