package controller.role;

import java.util.ArrayList;
import java.util.List;

/**
 * Бизнес-объект «Роль» (UML: {@code Role} - {@code id}, {@code name},
 * {@code permissions}).
 */
public class RoleBO {

    private Long id;
    private String name;
    private List<String> permissions = new ArrayList<String>();

    public RoleBO() {
    }

    public RoleBO(Long id, String name, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions == null ? new ArrayList<String>() : permissions;
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
        this.permissions = permissions == null ? new ArrayList<String>() : permissions;
    }
}
