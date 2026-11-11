package view.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Отдаёт главную страницу клиентского веб-интерфейса.
 *
 * <p>Сам файл лежит в ресурсах: {@code src/main/resources/static/index.html}.
 * Дополнительно в {@code WebAppServer} настроена раздача статики по префиксу
 * {@code /static}, поэтому страница доступна и по адресу
 * {@code http://localhost:8093/static/index.html}.</p>
 */
@Path("/")
public class StaticPageAPI {

    private static final String PAGE = "static/index.html";

    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response getIndexPage() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(PAGE);
        if (stream == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                    .entity("Файл " + PAGE + " не найден в classpath")
                    .build();
        }

        try {
            return Response.ok(readUtf8(stream)).build();
        } catch (IOException exception) {
            return Response.serverError()
                    .type(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                    .entity("Не удалось прочитать страницу: " + exception.getMessage())
                    .build();
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
                // закрытие потока не влияет на уже прочитанное содержимое
            }
        }
    }

    private static String readUtf8(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int read;
        while ((read = stream.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }
}
