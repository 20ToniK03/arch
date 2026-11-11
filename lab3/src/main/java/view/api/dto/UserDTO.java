package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO пользователя для административного интерфейса.
 */
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private boolean blocked;
    private List<String> roles = new ArrayList<String>();

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, boolean blocked, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.blocked = blocked;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
