package view.api;

import controller.FacadeController;
import view.api.dto.ActionStatusDTO;
import view.api.dto.CreateNewsRequestDTO;
import view.api.dto.NewsItemDTO;
import view.api.dto.UpdateNewsRequestDTO;

import javax.ws.rs.Consumes;
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
 * API журналиста (актёр «Журналист», use case «Создать/Редактировать/Удалить
 * новость») - лабораторная работа №3.
 *
 * <p>Работает через {@link FacadeController} (внутри - {@code JournalistBO}:
 * {@code createNews}, {@code editNews}, {@code deleteNews}).</p>
 */
@Path("/creator")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class CreatorAPI {

    private final FacadeController facade = FacadeController.getInstance();

    @POST
    @Path("/news")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response createNews(CreateNewsRequestDTO request) {
        if (request == null || isBlank(request.getTitle())) {
            return error(Response.Status.BAD_REQUEST, "Поле title обязательно");
        }
        if (request.getCategoryId() == null) {
            return error(Response.Status.BAD_REQUEST, "Поле categoryId обязательно");
        }
        NewsItemDTO created = facade.createNews(request);
        if (created == null) {
            return error(Response.Status.BAD_REQUEST,
                    "Категория с id = " + request.getCategoryId() + " не найдена");
        }
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/news/{id}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response updateNews(@PathParam("id") Long id, UpdateNewsRequestDTO request) {
        NewsItemDTO updated = facade.updateNews(id, request);
        if (updated == null) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/news/{id}")
    public Response deleteNews(@PathParam("id") Long id) {
        if (!facade.deleteNews(id)) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(ActionStatusDTO.ok("Новость " + id + " удалена")).build();
    }

    /** Опубликовать новость (IPublishable.publish, состояние Published). */
    @POST
    @Path("/news/{id}/publish")
    public Response publishNews(@PathParam("id") Long id) {
        NewsItemDTO published = facade.publishNews(id);
        if (published == null) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(published).build();
    }

    /** Снять с публикации (IPublishable.unpublish, состояние Draft). */
    @POST
    @Path("/news/{id}/unpublish")
    public Response unpublishNews(@PathParam("id") Long id) {
        NewsItemDTO unpublished = facade.unpublishNews(id);
        if (unpublished == null) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(unpublished).build();
    }

    /** Перевести в архив (IPublishable.archive, состояние Archived). */
    @POST
    @Path("/news/{id}/archive")
    public Response archiveNews(@PathParam("id") Long id) {
        NewsItemDTO archived = facade.archiveNews(id);
        if (archived == null) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(archived).build();
    }

    /** Добавить тег (News 0..* -- 0..* Tag). */
    @POST
    @Path("/news/{id}/tags")
    public Response addTag(@PathParam("id") Long id, @QueryParam("tag") String tag) {
        if (isBlank(tag)) {
            return error(Response.Status.BAD_REQUEST, "Параметр tag обязателен");
        }
        if (!facade.addTag(id, tag)) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(ActionStatusDTO.ok("К новости " + id + " добавлен тег «" + tag + "»")).build();
    }

    /** Прикрепить медиафайл (вспомогательный метод). */
    @POST
    @Path("/news/{id}/media")
    public Response attachMedia(@PathParam("id") Long id, @QueryParam("url") String url) {
        if (isBlank(url)) {
            return error(Response.Status.BAD_REQUEST, "Параметр url обязателен");
        }
        if (!facade.attachMedia(id, url)) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(ActionStatusDTO.ok("К новости " + id + " прикреплён файл " + url)).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Response error(Response.Status status, String message) {
        return Response.status(status).entity(ActionStatusDTO.error(message)).build();
    }
}
