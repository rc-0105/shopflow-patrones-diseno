package co.edu.unbosque.shopflow.pagos.adaptadores;

import co.edu.unbosque.shopflow.externos.quickpay.QuickPayRequest;
import co.edu.unbosque.shopflow.externos.quickpay.QuickPayResult;
import co.edu.unbosque.shopflow.externos.quickpay.QuickPaySdk;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;

// Adapter: hace que el SDK de QuickPay se vea como una PasarelaDePago de ShopFlow.
public class QuickPayAdapter implements PasarelaDePago {

    private static final String MONEDA = "USD";

    private final QuickPaySdk sdk;

    public QuickPayAdapter(QuickPaySdk sdk) {
        this.sdk = sdk;
    }
    public QuickPayAdapter() {
        this(new QuickPaySdk());
    }

    @Override
    public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
        // QuickPay recibe el monto como double; la conversión se queda aquí y no en el dominio.
        QuickPayResult resultado = sdk.pay(new QuickPayRequest(referencia, montoUsd.doubleValue(), MONEDA));

        // Convierte el estado de QuickPay en un ResultadoPago de ShopFlow.
        return switch (resultado.status()) {
            case OK -> ResultadoPago.aprobado(resultado.transactionId());
            case REJECTED -> ResultadoPago.rechazado("Pago rechazado por QuickPay: " + resultado.detail());
            case ERROR -> ResultadoPago.rechazado("QuickPay no pudo procesar el pago: " + resultado.detail());
        };
    }
}
