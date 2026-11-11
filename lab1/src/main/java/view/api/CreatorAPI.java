package view.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API создателя/редактора - лабораторная работа №1.
 *
 * <p>Методы изменения данных нельзя открыть простым переходом по ссылке в браузере,
 * поэтому для их проверки используется файл {@code requests.http} (IntelliJ IDEA
 * запускает такие запросы по значку рядом со строкой) или curl.</p>
 */
@Path("/creator")
@Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
public class CreatorAPI {

    /** Создать новость. POST /creator/news?title=...&text=... */
    @POST
    @Path("/news")
    public Response createNews(@QueryParam("title") String title,
                               @QueryParam("text") String text) {
        if (isBlank(title)) {
            return badRequest("Параметр title обязателен");
        }
        return ok("Новость «" + title + "» создана (заглушка лабораторной работы №1).");
    }

    /** Редактировать новость. PUT /creator/news/1?title=...&text=... */
    @PUT
    @Path("/news/{id}")
    public Response updateNews(@PathParam("id") Long id,
                               @QueryParam("title") String title,
                               @QueryParam("text") String text) {
        return ok("Новость с id = " + id + " изменена (заглушка лабораторной работы №1).");
    }

    /** Удалить новость. DELETE /creator/news/1 */
    @DELETE
    @Path("/news/{id}")
    public Response deleteNews(@PathParam("id") Long id) {
        return ok("Новость с id = " + id + " удалена (заглушка лабораторной работы №1).");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Response ok(String message) {
        return Response.ok(message).type(MediaType.TEXT_PLAIN + ";charset=UTF-8").build();
    }

    private static Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                .build();
    }
}
