package view.api;

import view.api.dto.ActionStatusDTO;
import view.api.dto.CategoryDTO;
import view.api.dto.CategoryListDTO;
import view.api.dto.CommentDTO;
import view.api.dto.CreateCommentRequestDTO;
import view.api.dto.NewsItemDTO;
import view.api.dto.NewsListDTO;
import view.api.dto.NewsTitleDTO;
import view.api.dto.UpdateCommentRequestDTO;

import javax.ws.rs.Consumes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * API читателя - лабораторная работа №2.
 *
 * <p>Отличие от первой работы: каждый метод возвращает не строку, а DTO-объект.
 * Благодаря провайдеру Jackson RESTEasy автоматически сериализует DTO в JSON.
 * Данные пока тестовые - «зашиты» прямо в методах, базы данных ещё нет.</p>
 */
@Path("/reader")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class ReaderAPI {

    /** Все новости. GET /reader/news/all */
    @GET
    @Path("/news/all")
    public Response getAllNews() {
        List<NewsTitleDTO> items = new ArrayList<NewsTitleDTO>();
        items.add(new NewsTitleDTO(1L, "Запущена новая версия городского портала",
                "Технологии", "2026-11-09 10:00"));
        items.add(new NewsTitleDTO(2L, "Открытие новой станции метро",
                "Общество", "2026-11-07 18:30"));
        items.add(new NewsTitleDTO(3L, "Чемпионат города по плаванию",
                "Спорт", "2026-11-01 12:15"));

        return Response.ok(new NewsListDTO(items)).build();
    }

    /** Одна новость по id. GET /reader/news/1 */
    @GET
    @Path("/news/{id}")
    public Response getNewsById(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Идентификатор новости должен быть положительным числом"))
                    .build();
        }

        NewsItemDTO news = new NewsItemDTO(
                id,
                "Запущена новая версия городского портала",
                "Портал стал быстрее и получил удобный поиск по новостям.",
                "Городской портал обновился: ускорена загрузка страниц, появился умный поиск "
                        + "и раздел с персональными подборками новостей.",
                "Технологии",
                "Редакция",
                "2026-11-09 10:00",
                "Москва",
                Arrays.asList("технологии", "портал", "обновление"),
                new ArrayList<String>(),
                42
        );

        return Response.ok(news).build();
    }

    /** Новости за последнюю неделю. GET /reader/news/last_week */
    @GET
    @Path("/news/last_week")
    public Response getLastWeekNews() {
        List<NewsTitleDTO> items = new ArrayList<NewsTitleDTO>();
        items.add(new NewsTitleDTO(1L, "Запущена новая версия городского портала",
                "Технологии", "2026-11-09 10:00"));
        items.add(new NewsTitleDTO(2L, "Открытие новой станции метро",
                "Общество", "2026-11-07 18:30"));

        return Response.ok(new NewsListDTO(items)).build();
    }

    /** Поиск новостей по ключевым словам. GET /reader/news/search?keywords=метро */
    @GET
    @Path("/news/search")
    public Response searchNews(@QueryParam("keywords") String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Параметр keywords обязателен, например: ?keywords=метро"))
                    .build();
        }

        List<NewsTitleDTO> items = new ArrayList<NewsTitleDTO>();
        items.add(new NewsTitleDTO(2L, "Открытие новой станции метро",
                "Общество", "2026-11-07 18:30"));

        return Response.ok(new NewsListDTO(items)).build();
    }

    /** Новости региона. GET /reader/news/region?region=Москва */
    @GET
    @Path("/news/region")
    public Response getNewsByRegion(@QueryParam("region") String region) {
        if (region == null || region.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Параметр region обязателен, например: ?region=Москва"))
                    .build();
        }

        List<NewsTitleDTO> items = new ArrayList<NewsTitleDTO>();
        items.add(new NewsTitleDTO(1L, "Запущена новая версия городского портала",
                "Технологии", "2026-11-09 10:00"));

        return Response.ok(new NewsListDTO(items)).build();
    }

    /** Список категорий. GET /reader/categories */
    @GET
    @Path("/categories")
    public Response getCategories() {
        List<CategoryDTO> categories = new ArrayList<CategoryDTO>();
        categories.add(new CategoryDTO(1L, "Технологии", "IT, гаджеты и инновации"));
        categories.add(new CategoryDTO(2L, "Общество", "Городские события и жизнь горожан"));
        categories.add(new CategoryDTO(3L, "Спорт", "Соревнования и спортивные результаты"));
        categories.add(new CategoryDTO(4L, "Культура", "Выставки, театр, музыка"));

        return Response.ok(new CategoryListDTO(categories)).build();
    }

    /** Добавить новость в закладки. POST /reader/news/1/bookmark */
    @POST
    @Path("/news/{id}/bookmark")
    public Response addBookmark(@PathParam("id") Long id) {
        return Response.ok(ActionStatusDTO.ok("Новость " + id + " добавлена в закладки")).build();
    }

    /** Оставить комментарий. POST /reader/comments */
    @POST
    @Path("/comments")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response createComment(CreateCommentRequestDTO request) {
        if (request == null || request.getNewsId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Поле newsId обязательно"))
                    .build();
        }
        if (isBlank(request.getText())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Поле text обязательно"))
                    .build();
        }

        CommentDTO comment = new CommentDTO(
                1L,
                request.getNewsId(),
                isBlank(request.getAuthor()) ? "Аноним" : request.getAuthor(),
                request.getText(),
                "2026-11-10 12:00",
                null
        );

        return Response.status(Response.Status.CREATED).entity(comment).build();
    }

    /** Редактировать комментарий. PUT /reader/comments/1 */
    @PUT
    @Path("/comments/{id}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response updateComment(@PathParam("id") Long id, UpdateCommentRequestDTO request) {
        if (request == null || isBlank(request.getText())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Поле text обязательно"))
                    .build();
        }

        CommentDTO comment = new CommentDTO(
                id, 1L, "Иван", request.getText(), "2026-11-10 12:05", null);

        return Response.ok(comment).build();
    }

    /** Удалить комментарий. DELETE /reader/comments/1 */
    @DELETE
    @Path("/comments/{id}")
    public Response deleteComment(@PathParam("id") Long id) {
        return Response.ok(ActionStatusDTO.ok("Комментарий " + id + " удалён")).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
