package co.edu.unbosque.shopflow.facturacion.mexico;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/** RFC mexicano: 3 o 4 letras, fecha AAMMDD y homoclave de 3 caracteres (ej. GODE561231GR8). */
class ValidadorRfc implements ValidadorIdentificacion {

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.toUpperCase().matches("[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}")) {
            throw new IllegalArgumentException("Documento inválido para México: se esperaba un RFC (ej. GODE561231GR8)");
        }
    }
}
