package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO роли пользователя.
 */
public class RoleDTO {

    private Long id;
    private String name;
    private List<String> permissions = new ArrayList<String>();

    public RoleDTO() {
    }

    public RoleDTO(Long id, String name, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
