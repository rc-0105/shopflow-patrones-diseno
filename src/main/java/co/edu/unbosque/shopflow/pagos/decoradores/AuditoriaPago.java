package co.edu.unbosque.shopflow.pagos.decoradores;

import co.edu.unbosque.shopflow.pagos.EntradaAuditoria;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.RegistroAuditoria;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;
import java.time.Instant;

// Decorador: registra cada cobro (proveedor, monto, resultado y duración) en la auditoría.
public class AuditoriaPago extends PasarelaDecorator {

    private final String proveedor;
    private final RegistroAuditoria registro;

    public AuditoriaPago(PasarelaDePago envuelta, String proveedor, RegistroAuditoria registro) {
        super(envuelta);
        this.proveedor = proveedor;
        this.registro = registro;
    }

    @Override
    public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
        long inicio = System.nanoTime();
        try {
            ResultadoPago resultado = super.cobrar(montoUsd, referencia);
            registrar(referencia, montoUsd, resultado.aprobado() ? "APROBADO" : "RECHAZADO: " + resultado.mensaje(), inicio);
            return resultado;
        } catch (RuntimeException e) {
            // También se registran los cobros que fallan, y el error sigue su camino.
            registrar(referencia, montoUsd, "ERROR: " + e.getMessage(), inicio);
            throw e;
        }
    }

    // Calcula cuánto tardó el cobro y guarda la entrada.
    private void registrar(String referencia, BigDecimal montoUsd, String resultado, long inicio) {
        long duracionMs = (System.nanoTime() - inicio) / 1_000_000;
        registro.registrar(new EntradaAuditoria(Instant.now(), proveedor, referencia, montoUsd, resultado, duracionMs));
    }
}
