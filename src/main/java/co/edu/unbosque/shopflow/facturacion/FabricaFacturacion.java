package co.edu.unbosque.shopflow.facturacion;

/**
 * Fábrica abstracta: crea la familia de piezas de facturación de un país.
 * Un país nuevo es una fábrica nueva anotada con @Component; nada más cambia.
 */
public interface FabricaFacturacion {
    String pais();
    CalculadoraImpuestos crearCalculadora();
    ValidadorIdentificacion crearValidador();
    GeneradorComprobante crearGenerador();
}
