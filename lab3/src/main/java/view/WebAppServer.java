package view;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import view.api.SingletonAPI;

/**
 * Веб-сервер приложения (лабораторная работа №3).
 *
 * <p>Кроме JAX-RS-приложения сервер раздаёт статические файлы из каталога
 * {@code src/main/resources/static} по префиксу {@code /static}. Это позволяет
 * открыть клиентский интерфейс по адресу {@code http://localhost:8093/}
 * (главная страница отдаётся {@code StaticPageAPI}) или напрямую
 * {@code http://localhost:8093/static/index.html}.</p>
 */
public class WebAppServer {

    public static final String HOST = "localhost";
    public static final int PORT = 8093;

    public static UndertowJaxrsServer startWebServer() {
        UndertowJaxrsServer webServer = new UndertowJaxrsServer();

        ResourceHandler staticFiles = new ResourceHandler(
                new ClassPathResourceManager(WebAppServer.class.getClassLoader(), "static/"));
        staticFiles.setDirectoryListingEnabled(false);
        webServer.addResourcePrefixPath("/static", staticFiles);

        webServer.start(Undertow.builder().addHttpListener(PORT, HOST));
        webServer.deploy(SingletonAPI.class);

        System.out.println("=================================================");
        System.out.println(" Сервис новостей - лабораторная работа №3 (BO + Facade)");
        System.out.println(" Клиентский интерфейс: http://" + HOST + ":" + PORT + "/");
        System.out.println(" API читателя:         http://" + HOST + ":" + PORT + "/reader/news/all");
        System.out.println(" API создателя:        http://" + HOST + ":" + PORT + "/creator/news");
        System.out.println(" Остановка: Ctrl + C");
        System.out.println("=================================================");
        return webServer;
    }

    public static void main(String[] args) {
        startWebServer();
    }
}
