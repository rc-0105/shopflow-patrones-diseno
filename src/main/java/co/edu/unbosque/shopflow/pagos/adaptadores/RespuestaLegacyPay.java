package co.edu.unbosque.shopflow.pagos.adaptadores;



// El SDK responde un texto como "00|LP4051": 00 aprobado, 51 fondos insuficientes, otro código no permitida.
record RespuestaLegacyPay(String codigo, String autorizacion) {

    private static final String APROBADO = "00";
    private static final String FONDOS_INSUFICIENTES = "51";

    // Separa "00|LP4051" en "00" y "LP4051". Si no hay "|", todo el texto es el código.
    static RespuestaLegacyPay desde(String texto) {
        int separador = texto.indexOf('|');
        if (separador < 0) {
            return new RespuestaLegacyPay(texto, "");
        }
        return new RespuestaLegacyPay(texto.substring(0, separador), texto.substring(separador + 1));
    }

    boolean aprobada() {
        return APROBADO.equals(codigo);
    }

    boolean fondosInsuficientes() {
        return FONDOS_INSUFICIENTES.equals(codigo);
    }
}
