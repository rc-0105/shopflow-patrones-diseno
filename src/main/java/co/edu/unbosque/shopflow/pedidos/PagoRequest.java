package co.edu.unbosque.shopflow.pedidos;

/** Cuerpo JSON de POST /api/pedidos/{id}/pago: {"proveedor": "LEGACYPAY"} o {"proveedor": "QUICKPAY"}. */
public record PagoRequest(String proveedor) { }
