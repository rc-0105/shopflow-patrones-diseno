package co.edu.unbosque.shopflow.facturacion.colombia;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/** Cédula o NIT colombiano: de 6 a 10 dígitos. */
class ValidadorCedulaNit implements ValidadorIdentificacion {

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.matches("\\d{6,10}")) {
            throw new IllegalArgumentException(
                    "Documento inválido para Colombia: la cédula o NIT debe tener entre 6 y 10 dígitos");
        }
    }
}
