package com.itechart.warehouse.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Data transfer object for role entity.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RoleDTO {
    private Short idRole;
    private String role;
}
