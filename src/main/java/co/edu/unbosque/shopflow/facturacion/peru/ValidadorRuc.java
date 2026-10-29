package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/** RUC peruano: 11 dígitos. */
class ValidadorRuc implements ValidadorIdentificacion {

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.matches("\\d{11}")) {
            throw new IllegalArgumentException("Documento inválido para Perú: el RUC debe tener 11 dígitos");
        }
    }
}
