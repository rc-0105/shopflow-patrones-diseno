package co.edu.unbosque.shopflow.facturacion;

import co.edu.unbosque.shopflow.pedidos.Pedido;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Cliente de la Abstract Factory: factura sin conocer ninguna clase concreta ni preguntar por el país. */
@Service
public class ServicioFacturacion {

    private final Map<String, FabricaFacturacion> fabricas;

    public ServicioFacturacion(List<FabricaFacturacion> fabricas) {
        this.fabricas = fabricas.stream()
                .collect(Collectors.toUnmodifiableMap(FabricaFacturacion::pais, Function.identity()));
    }

    public Comprobante facturar(Pedido pedido) {
        FabricaFacturacion fabrica = fabricaDe(pedido.pais());
        fabrica.crearValidador().validar(pedido.documentoCliente());

        CalculadoraImpuestos calculadora = fabrica.crearCalculadora();
        BigDecimal base = pedido.baseGravable();
        return fabrica.crearGenerador().generar(base, calculadora.nombre(), calculadora.calcular(base));
    }

    private FabricaFacturacion fabricaDe(String pais) {
        return Optional.ofNullable(fabricas.get(pais))
                .orElseThrow(() -> new IllegalArgumentException(
                        "País no soportado: " + pais + ". Países disponibles: " + fabricas.keySet()));
    }
}
