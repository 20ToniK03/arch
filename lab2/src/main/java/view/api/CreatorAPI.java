package view.api;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * API создателя/редактора - лабораторная работа №2.
 *
 * <p>Методы принимают DTO из тела запроса (JSON) и возвращают DTO-результат.
 * Данные тестовые: изменения нигде не сохраняются.</p>
 */
@Path("/creator")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class CreatorAPI {

    /** Создать новость. POST /creator/news */
    @POST
    @Path("/news")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response createNews(CreateNewsRequestDTO request) {
        if (request == null || isBlank(request.getTitle())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Поле title обязательно"))
                    .build();
        }

        NewsItemDTO created = new NewsItemDTO(
                10L,
                request.getTitle(),
                request.getAnnotation(),
                request.getText(),
                "Технологии",
                isBlank(request.getAuthor()) ? "Редакция" : request.getAuthor(),
                "2026-11-10 12:30",
                isBlank(request.getRegion()) ? "Москва" : request.getRegion(),
                request.getTags() == null ? new ArrayList<String>() : request.getTags(),
                new ArrayList<String>(),
                0
        );

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /** Изменить новость. PUT /creator/news/1 */
    @PUT
    @Path("/news/{id}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response updateNews(@PathParam("id") Long id, UpdateNewsRequestDTO request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Тело запроса обязательно"))
                    .build();
        }

        NewsItemDTO updated = new NewsItemDTO(
                id,
                isBlank(request.getTitle()) ? "Заголовок без изменений" : request.getTitle(),
                request.getAnnotation(),
                request.getText(),
                "Технологии",
                "Редакция",
                "2026-11-09 10:00",
                isBlank(request.getRegion()) ? "Москва" : request.getRegion(),
                request.getTags() == null ? new ArrayList<String>() : request.getTags(),
                new ArrayList<String>(),
                42
        );

        return Response.ok(updated).build();
    }

    /** Удалить новость. DELETE /creator/news/1 */
    @DELETE
    @Path("/news/{id}")
    public Response deleteNews(@PathParam("id") Long id) {
        return Response.ok(ActionStatusDTO.ok("Новость " + id + " удалена")).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
