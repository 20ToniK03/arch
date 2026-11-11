package view;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import view.api.SingletonAPI;

/**
 * Веб-сервер приложения (лабораторная работа №1).
 *
 * <p>Сервер построен на встроенном Undertow и RESTEasy. Приложение {@link SingletonAPI}
 * разворачивается в корне контекста, поэтому адреса методов имеют вид:
 * {@code http://localhost:8091/<@Path класса>/<@Path метода>}.</p>
 *
 * <p>Запуск: метод {@link #main(String[])} либо {@code java -jar target/news-service-lab1.jar}.</p>
 */
public class WebAppServer {

    /** Хост, на котором поднимается сервер. */
    public static final String HOST = "localhost";

    /** Порт лабораторной работы №1. */
    public static final int PORT = 8091;

    /**
     * Поднимает веб-сервер и деплоит в него JAX-RS-приложение с API.
     *
     * @return запущенный сервер (нужен для корректной остановки в тестах)
     */
    public static UndertowJaxrsServer startWebServer() {
        UndertowJaxrsServer webServer = new UndertowJaxrsServer();

        webServer.start(Undertow.builder().addHttpListener(PORT, HOST));

        webServer.deploy(SingletonAPI.class);

        System.out.println("=================================================");
        System.out.println(" Сервис новостей - лабораторная работа №1");
        System.out.println(" Сервер запущен: http://" + HOST + ":" + PORT);
        System.out.println(" Читатель:  http://" + HOST + ":" + PORT + "/reader/news/all");
        System.out.println(" Создатель: http://" + HOST + ":" + PORT + "/creator/news");
        System.out.println(" Остановка: Ctrl + C");
        System.out.println("=================================================");
        return webServer;
    }

    public static void main(String[] args) {
        startWebServer();
    }
}
