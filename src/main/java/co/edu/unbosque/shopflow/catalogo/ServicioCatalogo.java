package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.List;

/** Interfaz propia de ShopFlow para consultar y administrar el catálogo. */
public interface ServicioCatalogo {
    Producto buscar(String sku);
    List<Producto> listar();
    Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio);
}
