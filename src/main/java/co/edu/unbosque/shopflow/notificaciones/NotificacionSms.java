package co.edu.unbosque.shopflow.notificaciones;

/** Producto concreto: un SMS se recorta a 160 caracteres. */
class NotificacionSms implements Notificacion {

    static final String CANAL = "SMS";
    private static final int MAX_CARACTERES = 160;

    private final BandejaNotificaciones bandeja;

    NotificacionSms(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void enviar(String destino, String mensaje) {
        String recortado = mensaje.length() > MAX_CARACTERES ? mensaje.substring(0, MAX_CARACTERES) : mensaje;
        bandeja.registrar(CANAL, destino, recortado);
    }
}
