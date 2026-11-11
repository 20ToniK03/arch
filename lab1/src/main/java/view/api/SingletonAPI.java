package view.api;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JAX-RS-приложение, реализующее паттерн «Одиночка» (Singleton).
 *
 * <p>Класс наследует {@link Application} и возвращает готовые экземпляры API через
 * {@link #getSingletons()}. RESTEasy создаёт эти объекты один раз и переиспользует
 * для обработки всех входящих HTTP-запросов, а не создаёт новый контроллер на каждый
 * запрос (в отличие от {@link Application#getClasses()}).</p>
 */
public class SingletonAPI extends Application {

    private final Set<Object> singletons;

    public SingletonAPI() {
        Set<Object> instances = new LinkedHashSet<Object>();
        instances.add(new ReaderAPI());
        instances.add(new CreatorAPI());
        instances.add(new AdminAPI());
        this.singletons = Collections.unmodifiableSet(instances);
    }

    /**
     * Возвращает единственные экземпляры всех API-контроллеров.
     */
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
