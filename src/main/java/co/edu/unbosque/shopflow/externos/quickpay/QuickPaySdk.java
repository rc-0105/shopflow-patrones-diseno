package co.edu.unbosque.shopflow.externos.quickpay;

import java.util.UUID;

/**
 * SDK del proveedor QuickPay (simulado). Código de un tercero: NO SE MODIFICA.
 * Recibe el monto como double en dólares y responde con un estado propio.
 */
public class QuickPaySdk {

    public QuickPayResult pay(QuickPayRequest request) {
        if (request.amount() <= 0) {
            return new QuickPayResult(QuickPayStatus.ERROR, null, "Invalid amount");
        }
        if (request.amount() > 5_000) {
            return new QuickPayResult(QuickPayStatus.REJECTED, null, "Amount over limit");
        }
        return new QuickPayResult(QuickPayStatus.OK, "QP-" + UUID.randomUUID().toString().substring(0, 8), "Approved");
    }
}
