package view.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.core.Application;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JAX-RS-приложение лабораторной работы №3.
 *
 * <p>Регистрирует единственные экземпляры контроллеров, провайдер Jackson
 * и фильтр CORS.</p>
 */
public class SingletonAPI extends Application {

    private final Set<Object> singletons = new LinkedHashSet<Object>();

    public SingletonAPI() {
        singletons.add(new ReaderAPI());
        singletons.add(new CreatorAPI());
        singletons.add(new AdminAPI());
        singletons.add(new StaticPageAPI());

        singletons.add(new JacksonJsonProvider());

        CorsFilter cors = new CorsFilter();
        cors.getAllowedOrigins().add("*");
        cors.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS");
        cors.setAllowedHeaders("Content-Type, Accept");
        cors.setAllowCredentials(false);
        singletons.add(cors);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
