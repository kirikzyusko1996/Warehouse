package com.itechart.warehouse.dto;

import com.itechart.warehouse.entity.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Data transfer object for role entity.
 */
@Getter
@Setter
@ToString
public class RoleDTO {
    private Short idRole;
    private String role;

    public static RoleDTO buildRoleDTO(Role role) {
        Assert.notNull(role, "Role is null");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setIdRole(role.getIdRole());
        roleDTO.setRole(role.getRole());
        return roleDTO;
    }
}
