package view.api;

import controller.FacadeController;
import view.api.dto.ActionStatusDTO;
import view.api.dto.CategoryDTO;
import view.api.dto.CategoryListDTO;
import view.api.dto.CommentDTO;
import view.api.dto.CreateCommentRequestDTO;
import view.api.dto.NewsItemDTO;
import view.api.dto.NewsListDTO;
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
import java.util.List;

/**
 * API читателя - лабораторная работа №3.
 *
 * <p>Просмотр/поиск/чтение новостей, категории и комментарии - через
 * {@link FacadeController}.</p>
 */
@Path("/reader")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class ReaderAPI {

    private final FacadeController facade = FacadeController.getInstance();

    @GET
    @Path("/news/all")
    public Response getAllNews() {
        return Response.ok(new NewsListDTO(facade.getAllNews())).build();
    }

    @GET
    @Path("/news/{id}")
    public Response getNewsById(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return error(Response.Status.BAD_REQUEST, "Идентификатор новости должен быть положительным числом");
        }
        NewsItemDTO news = facade.getNewsById(id);
        if (news == null) {
            return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
        }
        return Response.ok(news).build();
    }

    @GET
    @Path("/news/last_week")
    public Response getLastWeekNews() {
        return Response.ok(new NewsListDTO(facade.getLastWeekNews())).build();
    }

    @GET
    @Path("/news/search")
    public Response searchNews(@QueryParam("keywords") String keywords) {
        List<String> terms = splitKeywords(keywords);
        if (terms.isEmpty()) {
            return error(Response.Status.BAD_REQUEST, "Параметр keywords обязателен, например: ?keywords=метро");
        }
        return Response.ok(new NewsListDTO(facade.searchNews(terms))).build();
    }

    @GET
    @Path("/news/region")
    public Response getNewsByRegion(@QueryParam("region") String region) {
        if (region == null || region.trim().isEmpty()) {
            return error(Response.Status.BAD_REQUEST, "Параметр region обязателен, например: ?region=Москва");
        }
        return Response.ok(new NewsListDTO(facade.getNewsByRegion(region))).build();
    }

    @GET
    @Path("/categories")
    public Response getCategories() {
        List<CategoryDTO> categories = facade.getCategories();
        return Response.ok(new CategoryListDTO(categories)).build();
    }

    @POST
    @Path("/comments")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response createComment(CreateCommentRequestDTO request) {
        if (request == null || request.getNewsId() == null) {
            return error(Response.Status.BAD_REQUEST, "Поле newsId обязательно");
        }
        CommentDTO comment = facade.createComment(request);
        if (comment == null) {
            return error(Response.Status.NOT_FOUND,
                    "Новость с id = " + request.getNewsId() + " не найдена или текст пуст");
        }
        return Response.status(Response.Status.CREATED).entity(comment).build();
    }

    @PUT
    @Path("/comments/{id}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response updateComment(@PathParam("id") Long id, UpdateCommentRequestDTO request) {
        CommentDTO comment = facade.updateComment(id, request);
        if (comment == null) {
            return error(Response.Status.NOT_FOUND, "Комментарий с id = " + id + " не найден или текст пуст");
        }
        return Response.ok(comment).build();
    }

    @DELETE
    @Path("/comments/{id}")
    public Response deleteComment(@PathParam("id") Long id) {
        if (!facade.deleteComment(id)) {
            return error(Response.Status.NOT_FOUND, "Комментарий с id = " + id + " не найден");
        }
        return Response.ok(ActionStatusDTO.ok("Комментарий " + id + " удалён")).build();
    }

    private static List<String> splitKeywords(String raw) {
        List<String> result = new ArrayList<String>();
        if (raw == null) {
            return result;
        }
        for (String part : raw.split("[\\s,]+")) {
            if (!part.trim().isEmpty()) {
                result.add(part.trim());
            }
        }
        return result;
    }

    private static Response error(Response.Status status, String message) {
        return Response.status(status).entity(ActionStatusDTO.error(message)).build();
    }
}
