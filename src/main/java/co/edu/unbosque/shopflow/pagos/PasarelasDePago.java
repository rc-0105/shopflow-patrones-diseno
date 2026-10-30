package co.edu.unbosque.shopflow.pagos;

import java.util.Map;
import java.util.Optional;

/**
 * Pasarelas ya armadas (adapter + decoradores), indexadas por proveedor.
 * Envuelve el mapa en una clase propia para que Spring no lo confunda con un mapa de beans.
 */
public class PasarelasDePago {

    private final Map<String, PasarelaDePago> porProveedor;

    public PasarelasDePago(Map<String, PasarelaDePago> porProveedor) {
        this.porProveedor = Map.copyOf(porProveedor);
    }

    /** Devuelve la pasarela del proveedor o lanza IllegalArgumentException (400) si no existe. */
    public PasarelaDePago obtener(String proveedor) {
        return Optional.ofNullable(proveedor)
                .map(p -> porProveedor.get(p.trim().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proveedor de pago no válido: " + proveedor + ". Disponibles: " + porProveedor.keySet()));
    }
}
