package view.api;

import controller.FacadeController;
import view.api.dto.ActionStatusDTO;
import view.api.dto.RoleDTO;
import view.api.dto.RoleListDTO;
import view.api.dto.UserDTO;
import view.api.dto.UserListDTO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * API администратора (актёр «Администратор», use case «Администрирование») -
 * лабораторная работа №3.
 *
 * <p>Работает через {@link FacadeController} (внутри - {@code AdminBO}:
 * {@code blockUser}, {@code assignRole}, {@code moderateComment}).</p>
 */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class AdminAPI {

    private final FacadeController facade = FacadeController.getInstance();

    @GET
    @Path("/users")
    public Response getUsers() {
        List<UserDTO> users = facade.getUsers();
        return Response.ok(new UserListDTO(users)).build();
    }

    @GET
    @Path("/roles")
    public Response getRoles() {
        List<RoleDTO> roles = facade.getRoles();
        return Response.ok(new RoleListDTO(roles)).build();
    }

    @POST
    @Path("/users/{id}/block")
    public Response blockUser(@PathParam("id") Long id) {
        if (!facade.blockUser(id)) {
            return error(Response.Status.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }
        return Response.ok(ActionStatusDTO.ok("Пользователь " + id + " заблокирован")).build();
    }

    @PUT
    @Path("/users/{id}/role")
    public Response assignRole(@PathParam("id") Long id, @QueryParam("role") String role) {
        if (isBlank(role)) {
            return error(Response.Status.BAD_REQUEST, "Параметр role обязателен, например: ?role=ADMIN");
        }
        if (!facade.assignRole(id, role)) {
            return error(Response.Status.NOT_FOUND, "Пользователь или роль не найдены");
        }
        return Response.ok(ActionStatusDTO.ok("Пользователю " + id + " назначена роль «" + role + "»")).build();
    }

    @POST
    @Path("/comments/{id}/moderate")
    public Response moderateComment(@PathParam("id") Long id, @QueryParam("action") String action) {
        if (isBlank(action)) {
            return error(Response.Status.BAD_REQUEST, "Параметр action обязателен: approve или reject");
        }
        if (!facade.moderateComment(id, action)) {
            return error(Response.Status.NOT_FOUND,
                    "Комментарий с id = " + id + " не найден или некорректный action");
        }
        return Response.ok(ActionStatusDTO.ok("Комментарий " + id + " промодерирован (" + action + ")")).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Response error(Response.Status status, String message) {
        return Response.status(status).entity(ActionStatusDTO.error(message)).build();
    }
}
