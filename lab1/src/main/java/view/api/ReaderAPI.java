package view.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API читателя (клиента) - лабораторная работа №1.
 *
 * <p>На этом этапе методы возвращают текстовые заглушки: реальной бизнес-логики
 * ещё нет, проверяется только маршрутизация запросов и работа паттерна Singleton.
 * В лабораторной работе №2 ответы будут заменены на DTO (JSON), а в №3 - на
 * данные, полученные через фасад.</p>
 *
 * <p>Все GET-методы можно проверить в браузере, например:
 * {@code http://localhost:8091/reader/news/all}.</p>
 */
@Path("/reader")
@Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
public class ReaderAPI {

    /** Все новости. GET /reader/news/all */
    @GET
    @Path("/news/all")
    public Response getAllNews() {
        return ok("Список всех новостей. Заглушка лабораторной работы №1.");
    }

    /** Одна новость по идентификатору. GET /reader/news/1 */
    @GET
    @Path("/news/{id}")
    public Response getNewsById(@PathParam("id") Long id) {
        return ok("Новость с id = " + id + ". Заглушка лабораторной работы №1.");
    }

    /** Новости за последнюю неделю. GET /reader/news/last_week */
    @GET
    @Path("/news/last_week")
    public Response getLastWeekNews() {
        return ok("Новости за последнюю неделю. Заглушка лабораторной работы №1.");
    }

    /** Поиск новостей по ключевым словам. GET /reader/news/search?keywords=java */
    @GET
    @Path("/news/search")
    public Response searchNews(@QueryParam("keywords") String keywords) {
        if (isBlank(keywords)) {
            return badRequest("Параметр keywords обязателен, например: ?keywords=java");
        }
        return ok("Поиск новостей по ключевым словам: " + keywords
                + ". Заглушка лабораторной работы №1.");
    }

    /** Новости региона. GET /reader/news/region?region=Москва */
    @GET
    @Path("/news/region")
    public Response getNewsByRegion(@QueryParam("region") String region) {
        if (isBlank(region)) {
            return badRequest("Параметр region обязателен, например: ?region=Москва");
        }
        return ok("Новости региона " + region + ". Заглушка лабораторной работы №1.");
    }

    /** Список категорий. GET /reader/categories */
    @GET
    @Path("/categories")
    public Response getCategories() {
        return ok("Список категорий новостей. Заглушка лабораторной работы №1.");
    }

    /** Добавить новость в закладки. POST /reader/news/1/bookmark */
    @POST
    @Path("/news/{id}/bookmark")
    public Response addBookmark(@PathParam("id") Long id) {
        return ok("Новость с id = " + id + " добавлена в закладки "
                + "(заглушка лабораторной работы №1).");
    }

    /** Оставить комментарий к новости. POST /reader/comments?newsId=1&author=Иван&text=... */
    @POST
    @Path("/comments")
    public Response createComment(@QueryParam("newsId") Long newsId,
                                  @QueryParam("author") String author,
                                  @QueryParam("text") String text) {
        if (newsId == null) {
            return badRequest("Параметр newsId обязателен");
        }
        if (isBlank(text)) {
            return badRequest("Параметр text обязателен");
        }
        return ok("Комментарий к новости " + newsId + " от "
                + (isBlank(author) ? "анонимного читателя" : author)
                + " добавлен (заглушка лабораторной работы №1).");
    }

    /** Редактировать комментарий. PUT /reader/comments/1?text=... */
    @PUT
    @Path("/comments/{id}")
    public Response updateComment(@PathParam("id") Long id,
                                  @QueryParam("text") String text) {
        if (isBlank(text)) {
            return badRequest("Параметр text обязателен");
        }
        return ok("Комментарий с id = " + id + " изменён "
                + "(заглушка лабораторной работы №1).");
    }

    /** Удалить комментарий. DELETE /reader/comments/1 */
    @DELETE
    @Path("/comments/{id}")
    public Response deleteComment(@PathParam("id") Long id) {
        return ok("Комментарий с id = " + id + " удалён "
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
