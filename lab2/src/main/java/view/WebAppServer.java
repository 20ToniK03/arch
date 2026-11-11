package view;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import view.api.SingletonAPI;

/**
 * Веб-сервер приложения (лабораторная работа №2).
 *
 * <p>Порт изменён на 8092, чтобы лабораторные работы можно было запускать
 * одновременно (ЛР1 - 8091, ЛР2 - 8092, ЛР3 - 8093).</p>
 */
public class WebAppServer {

    public static final String HOST = "localhost";
    public static final int PORT = 8092;

    public static UndertowJaxrsServer startWebServer() {
        UndertowJaxrsServer webServer = new UndertowJaxrsServer();
        webServer.start(Undertow.builder().addHttpListener(PORT, HOST));
        webServer.deploy(SingletonAPI.class);

        System.out.println("=================================================");
        System.out.println(" Сервис новостей - лабораторная работа №2 (DTO + JSON)");
        System.out.println(" Сервер запущен: http://" + HOST + ":" + PORT);
        System.out.println(" Все новости:    http://" + HOST + ":" + PORT + "/reader/news/all");
        System.out.println(" Остановка: Ctrl + C");
        System.out.println("=================================================");
        return webServer;
    }

    public static void main(String[] args) {
        startWebServer();
    }
}
