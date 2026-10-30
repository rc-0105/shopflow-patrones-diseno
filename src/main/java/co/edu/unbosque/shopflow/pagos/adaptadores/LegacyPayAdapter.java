package co.edu.unbosque.shopflow.pagos.adaptadores;

import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayClient;
import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayTimeoutException;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Adapter: hace que el SDK de LegacyPay se vea como una PasarelaDePago de ShopFlow.
public class LegacyPayAdapter implements PasarelaDePago {

    private static final String MONEDA = "USD";

    private final LegacyPayClient cliente;

    public LegacyPayAdapter(LegacyPayClient cliente) {
        this.cliente = cliente;
    }

    public LegacyPayAdapter(double probabilidadTimeout) {
        this(new LegacyPayClient(probabilidadTimeout));
    }

    @Override
    public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
        String respuesta;
        try {
            respuesta = cliente.makeCharge(aCentavos(montoUsd), MONEDA, referencia);
        } catch (LegacyPayTimeoutException e) {
            // El error del SDK se cambia por uno de ShopFlow (responde 503).
            throw new PasarelaNoDisponibleException("LegacyPay no responde, intente más tarde", e);
        }
        return interpretar(respuesta);
    }

    // LegacyPay cobra en centavos: 145.66 USD pasa a 14566.
    private static long aCentavos(BigDecimal montoUsd) {
        return montoUsd.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    // Convierte la respuesta del SDK en un ResultadoPago de ShopFlow.
    private static ResultadoPago interpretar(String texto) {
        RespuestaLegacyPay respuesta = RespuestaLegacyPay.desde(texto);
        if (respuesta.aprobada()) {
            return ResultadoPago.aprobado(respuesta.autorizacion());
        }
        if (respuesta.fondosInsuficientes()) {
            return ResultadoPago.rechazado("Fondos insuficientes");
        }
        return ResultadoPago.rechazado("Transacción no permitida (código " + respuesta.codigo() + ")");
    }
}
