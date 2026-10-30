package co.edu.unbosque.shopflow.notificaciones;

import java.util.regex.Pattern;

/** Producto concreto (reto C): un WhatsApp exige un número con indicativo y antepone «✅» al mensaje. */
class NotificacionWhatsApp implements Notificacion {

    static final String CANAL = "WHATSAPP";
    private static final String PREFIJO = "✅ ";
    // «+», indicativo del país y número: entre 8 y 15 dígitos en total (formato E.164), p. ej. +573001234567.
    private static final Pattern NUMERO_CON_INDICATIVO = Pattern.compile("\\+\\d{8,15}");

    private final BandejaNotificaciones bandeja;

    NotificacionWhatsApp(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void validarDestino(String destino) {
        if (destino == null || !NUMERO_CON_INDICATIVO.matcher(destino).matches()) {
            throw new IllegalArgumentException(
                    "El canal WHATSAPP necesita un número con indicativo (ej. +573001234567), se recibió: " + destino);
        }
    }

    @Override
    public void enviar(String destino, String mensaje) {
        validarDestino(destino);
        bandeja.registrar(CANAL, destino, PREFIJO + mensaje);
    }
}
