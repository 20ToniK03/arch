package view.api;

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
 * API администратора - лабораторная работа №1.
 *
 * <p>Соответствует актёру «Администратор» и use case «Администрирование»
 * из UML-диаграммы (класс {@code Admin}: {@code blockUser}, {@code assignRole},
 * {@code moderateComment}). На этом этапе методы возвращают текстовые заглушки.</p>
 */
@Path("/admin")
@Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
public class AdminAPI {

    /** Список пользователей для модерации. GET /admin/users */
    @GET
    @Path("/users")
    public Response getUsers() {
        return ok("Список пользователей для модерации (заглушка лабораторной работы №1).");
    }

    /** Заблокировать пользователя. POST /admin/users/1/block */
    @POST
    @Path("/users/{id}/block")
    public Response blockUser(@PathParam("id") Long id) {
        return ok("Пользователь с id = " + id + " заблокирован "
                + "(заглушка лабораторной работы №1).");
    }

    /** Назначить пользователю роль. PUT /admin/users/1/role?role=ADMIN */
    @PUT
    @Path("/users/{id}/role")
    public Response assignRole(@PathParam("id") Long id,
                               @QueryParam("role") String role) {
        if (isBlank(role)) {
            return badRequest("Параметр role обязателен, например: ?role=ADMIN");
        }
        return ok("Пользователю " + id + " назначена роль «" + role + "» "
                + "(заглушка лабораторной работы №1).");
    }

    /** Модерировать комментарий. POST /admin/comments/1/moderate?action=approve */
    @POST
    @Path("/comments/{id}/moderate")
    public Response moderateComment(@PathParam("id") Long id,
                                    @QueryParam("action") String action) {
        if (isBlank(action)) {
            return badRequest("Параметр action обязателен: approve или reject");
        }
        return ok("Комментарий " + id + " промодерирован (" + action + ") "
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
