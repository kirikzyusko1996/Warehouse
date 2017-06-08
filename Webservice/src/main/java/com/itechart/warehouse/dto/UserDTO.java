package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.sql.Date;
import java.util.List;

/**
 * Data transfer object for user entity with all fields: including login and password.
 */
@Setter
@Getter
@ToString(exclude = {"roles", "warehouse", "warehouseCompany"})
@EqualsAndHashCode(exclude = {"roles", "warehouse", "warehouseCompany"})
public class UserDTO {
    private Long id;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String firstName;
    @NotBlank(message = "Last name can not be blank")
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String lastName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String patronymic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date dateOfBirth;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String city;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String street;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String house;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String apartment;
    @Email(message = "Illegal email")
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String email;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String login;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String password;
    private List<RoleDTO> roles;
    private Warehouse warehouse;
    private WarehouseCompany warehouseCompany;
}
