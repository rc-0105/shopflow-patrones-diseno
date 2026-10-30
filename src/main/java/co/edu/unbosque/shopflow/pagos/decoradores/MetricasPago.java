package co.edu.unbosque.shopflow.pagos.decoradores;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import java.math.BigDecimal;

// Cuenta los cobros y los aprobados, y muestra la tasa de aprobación.
public class MetricasPago extends PasarelaDecorator {

	private final String proveedor;
	private int cobros;
	private int aprobados;

	public MetricasPago(PasarelaDePago envuelta, String proveedor) {
		super(envuelta);
		this.proveedor = proveedor;
	}

	@Override
	public synchronized ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		boolean aprobado = false;
		try {
			ResultadoPago resultado = super.cobrar(montoUsd, referencia);
			aprobado = resultado.aprobado();
			return resultado;
		} finally {
			cobros++;
			if (aprobado) {
				aprobados++;
			}
			System.out.println("Métricas " + proveedor + ": " + aprobados + "/" + cobros + " aprobados ("
					+ tasaDeAprobacion() + " %)");
		}
	}

	public synchronized int cobros() {
		return cobros;
	}

	public synchronized int aprobados() {
		return aprobados;
	}

	// Porcentaje de cobros aprobados, de 0 a 100.
	public synchronized double tasaDeAprobacion() {
		return cobros == 0 ? 0.0 : aprobados * 100.0 / cobros;
	}
}
