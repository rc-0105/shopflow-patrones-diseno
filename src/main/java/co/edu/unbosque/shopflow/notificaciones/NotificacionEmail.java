package co.edu.unbosque.shopflow.notificaciones;

/** Producto concreto: un correo exige un contacto con «@» y lleva asunto. */
class NotificacionEmail implements Notificacion {

    static final String CANAL = "EMAIL";
    private static final String ASUNTO = "Pago aprobado";

    private final BandejaNotificaciones bandeja;

    NotificacionEmail(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void validarDestino(String destino) {
        if (destino == null || !destino.contains("@")) {
            throw new IllegalArgumentException("El canal EMAIL necesita un contacto con @, se recibió: " + destino);
        }
    }

    @Override
    public void enviar(String destino, String mensaje) {
        validarDestino(destino);
        bandeja.registrar(CANAL, destino, "[" + ASUNTO + "] " + mensaje);
    }
}
