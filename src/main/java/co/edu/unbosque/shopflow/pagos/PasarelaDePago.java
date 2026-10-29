package co.edu.unbosque.shopflow.pagos;

import java.math.BigDecimal;

/** Interfaz propia de ShopFlow (Target). El dominio solo conoce esta interfaz. */
public interface PasarelaDePago {
    ResultadoPago cobrar(BigDecimal montoUsd, String referencia);
}
