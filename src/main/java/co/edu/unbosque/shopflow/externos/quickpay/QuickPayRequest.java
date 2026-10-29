package co.edu.unbosque.shopflow.externos.quickpay;

/** Petición del SDK de QuickPay. NO SE MODIFICA. */
public record QuickPayRequest(String orderId, double amount, String currencyIso) { }
