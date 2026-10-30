package co.edu.unbosque.shopflow.catalogo;

import co.edu.unbosque.shopflow.comun.AccesoDenegadoException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Proxy de protección: las consultas pasan directo; cambiar un precio solo lo
 * puede hacer el rol ADMIN. Se crea por petición, porque el rol cambia en cada una.
 */
public class CatalogoProteccionProxy implements ServicioCatalogo {

    private static final String ROL_ADMIN = "ADMIN";

    private final ServicioCatalogo real;
    private final String rol;

    public CatalogoProteccionProxy(ServicioCatalogo real, String rol) {
        this.real = real;
        this.rol = rol;
    }

    @Override
    public Producto buscar(String sku) {
        return real.buscar(sku);
    }

    @Override
    public List<Producto> listar() {
        return real.listar();
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        if (rol == null || !ROL_ADMIN.equalsIgnoreCase(rol.trim())) {
            throw new AccesoDenegadoException("Solo el rol ADMIN puede cambiar precios");
        }
        return real.actualizarPrecio(sku, nuevoPrecio);
    }
}
