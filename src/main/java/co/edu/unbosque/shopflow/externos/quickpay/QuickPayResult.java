package co.edu.unbosque.shopflow.externos.quickpay;

/** Respuesta del SDK de QuickPay. NO SE MODIFICA. */
public record QuickPayResult(QuickPayStatus status, String transactionId, String detail) { }
