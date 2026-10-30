package co.edu.unbosque.shopflow.pagos;

import co.edu.unbosque.shopflow.pagos.adaptadores.LegacyPayAdapter;
import co.edu.unbosque.shopflow.pagos.adaptadores.QuickPayAdapter;
import co.edu.unbosque.shopflow.pagos.decoradores.AuditoriaPago;
import co.edu.unbosque.shopflow.pagos.decoradores.ReintentoPago;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Punto de composición: el único lugar que conoce los adapters y decoradores concretos.
 * Las capas dependen de application.properties; cambiarlas no exige modificar ninguna clase.
 * Orden: la auditoría va por fuera del reintento, así queda una sola entrada por cobro.
 */
@Configuration
public class ConfiguracionPagos {

    @Value("${shopflow.legacypay.probabilidad-timeout}")
    private double probabilidadTimeout;

    @Value("${shopflow.pagos.reintentos}")
    private boolean reintentos;

    @Value("${shopflow.pagos.max-intentos}")
    private int maxIntentos;

    @Value("${shopflow.pagos.auditoria}")
    private boolean auditoria;

    @Bean
    public PasarelasDePago pasarelasDePago(RegistroAuditoria registro) {
        return new PasarelasDePago(Map.of(
                "LEGACYPAY", armar("LEGACYPAY", new LegacyPayAdapter(probabilidadTimeout), registro),
                "QUICKPAY", armar("QUICKPAY", new QuickPayAdapter(), registro)));
    }

    private PasarelaDePago armar(String proveedor, PasarelaDePago adapter, RegistroAuditoria registro) {
        PasarelaDePago pasarela = adapter;
        if (reintentos) {
            pasarela = new ReintentoPago(pasarela, maxIntentos);
        }
        if (auditoria) {
            pasarela = new AuditoriaPago(pasarela, proveedor, registro);
        }
        return pasarela;
    }
}
