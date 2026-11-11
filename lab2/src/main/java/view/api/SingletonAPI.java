package view.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.core.Application;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JAX-RS-приложение лабораторной работы №2.
 *
 * <p>Помимо единственных экземпляров {@link ReaderAPI} и {@link CreatorAPI} здесь
 * регистрируется провайдер Jackson: именно он превращает возвращаемые DTO в JSON
 * (и разбирает JSON из тела запроса в DTO).</p>
 */
public class SingletonAPI extends Application {

    private final Set<Object> singletons = new LinkedHashSet<Object>();

    public SingletonAPI() {
        singletons.add(new ReaderAPI());
        singletons.add(new CreatorAPI());

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
