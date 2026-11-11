package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO списка пользователей для модерации.
 */
public class UserListDTO {

    private List<UserDTO> users = new ArrayList<UserDTO>();
    private int total;

    public UserListDTO() {
    }

    public UserListDTO(List<UserDTO> users) {
        this.users = users;
        this.total = users == null ? 0 : users.size();
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
        this.total = users == null ? 0 : users.size();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
