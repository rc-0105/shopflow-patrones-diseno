package co.edu.unbosque.shopflow.facturacion;

/** Producto abstracto de la familia: el documento del cliente. Lanza IllegalArgumentException si no es válido. */
public interface ValidadorIdentificacion {
    void validar(String documento);
}
