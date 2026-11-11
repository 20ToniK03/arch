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

    /** Прикрепить медиафайл. POST /creator/news/1/media?url=https://... */
    @POST
    @Path("/news/{id}/media")
    public Response attachMedia(@PathParam("id") Long id,
                                @QueryParam("url") String url) {
        if (isBlank(url)) {
            return badRequest("Параметр url обязателен");
        }
        return ok("К новости " + id + " прикреплён медиафайл " + url
                + " (заглушка лабораторной работы №1).");
    }

    /** Добавить тег. POST /creator/news/1/tags?tag=java */
    @POST
    @Path("/news/{id}/tags")
    public Response addTag(@PathParam("id") Long id,
                           @QueryParam("tag") String tag) {
        if (isBlank(tag)) {
            return badRequest("Параметр tag обязателен");
        }
        return ok("К новости " + id + " добавлен тег «" + tag + "» "
                + "(заглушка лабораторной работы №1).");
    }

    /** Ответить на комментарий. POST /creator/comments/1/reply?text=... */
    @POST
    @Path("/comments/{id}/reply")
    public Response replyToComment(@PathParam("id") Long id,
                                   @QueryParam("text") String text) {
        if (isBlank(text)) {
            return badRequest("Параметр text обязателен");
        }
        return ok("Ответ на комментарий " + id + " добавлен "
                + "(заглушка лабораторной работы №1).");
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
