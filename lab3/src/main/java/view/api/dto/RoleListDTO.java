package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO списка ролей.
 */
public class RoleListDTO {

    private List<RoleDTO> roles = new ArrayList<RoleDTO>();
    private int total;

    public RoleListDTO() {
    }

    public RoleListDTO(List<RoleDTO> roles) {
        this.roles = roles;
        this.total = roles == null ? 0 : roles.size();
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
        this.total = roles == null ? 0 : roles.size();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
