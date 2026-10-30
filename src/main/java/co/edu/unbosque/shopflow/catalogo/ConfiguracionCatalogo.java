package co.edu.unbosque.shopflow.catalogo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Único lugar que sabe que el catálogo de la aplicación es un proxy de caché. */
@Configuration
public class ConfiguracionCatalogo {

    @Bean
    public ServicioCatalogo servicioCatalogo() {
        return new CatalogoCacheProxy(new CatalogoRemoto());
    }
}
