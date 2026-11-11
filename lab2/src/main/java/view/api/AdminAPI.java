package view.api;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * API администратора - лабораторная работа №2.
 *
 * <p>Соответствует актёру «Администратор» и классу {@code Admin}
 * ({@code blockUser}, {@code assignRole}, {@code moderateComment}) из UML.
 * Методы возвращают DTO-объекты с тестовыми данными.</p>
 */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class AdminAPI {

    /** Список пользователей. GET /admin/users */
    @GET
    @Path("/users")
    public Response getUsers() {
        List<UserDTO> users = new ArrayList<UserDTO>();
        users.add(new UserDTO(1L, "Иван Петров", "ivan@example.com", false,
                Arrays.asList("USER")));
        users.add(new UserDTO(2L, "Мария Соколова", "maria@example.com", false,
                Arrays.asList("JOURNALIST")));
        users.add(new UserDTO(3L, "Алексей Админов", "admin@example.com", false,
                Arrays.asList("ADMIN")));
        return Response.ok(new UserListDTO(users)).build();
    }

    /** Список ролей. GET /admin/roles */
    @GET
    @Path("/roles")
    public Response getRoles() {
        List<RoleDTO> roles = new ArrayList<RoleDTO>();
        roles.add(new RoleDTO(1L, "GUEST", Arrays.asList("news.view")));
        roles.add(new RoleDTO(2L, "USER", Arrays.asList("news.view", "news.comment", "news.favorite")));
        roles.add(new RoleDTO(3L, "JOURNALIST", Arrays.asList("news.view", "news.create", "news.edit", "news.delete")));
        roles.add(new RoleDTO(4L, "ADMIN", Arrays.asList("user.block", "user.role", "comment.moderate")));
        return Response.ok(new RoleListDTO(roles)).build();
    }

    /** Заблокировать пользователя. POST /admin/users/1/block */
    @POST
    @Path("/users/{id}/block")
    public Response blockUser(@PathParam("id") Long id) {
        return Response.ok(ActionStatusDTO.ok("Пользователь " + id + " заблокирован")).build();
    }

    /** Назначить роль. PUT /admin/users/1/role?role=ADMIN */
    @PUT
    @Path("/users/{id}/role")
    public Response assignRole(@PathParam("id") Long id,
                               @QueryParam("role") String role) {
        if (isBlank(role)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Параметр role обязателен"))
                    .build();
        }
        return Response.ok(ActionStatusDTO.ok(
                "Пользователю " + id + " назначена роль «" + role + "»")).build();
    }

    /** Модерировать комментарий. POST /admin/comments/1/moderate?action=approve */
    @POST
    @Path("/comments/{id}/moderate")
    public Response moderateComment(@PathParam("id") Long id,
                                    @QueryParam("action") String action) {
        if (isBlank(action)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ActionStatusDTO.error("Параметр action обязателен: approve или reject"))
                    .build();
        }
        return Response.ok(ActionStatusDTO.ok(
                "Комментарий " + id + " промодерирован (" + action + ")")).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
