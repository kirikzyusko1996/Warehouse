package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

import java.sql.Date;
import java.util.List;

/**
 * Data transfer object for user entity with all fields: including login and password.
 */
@Setter
@Getter
@lombok.ToString(exclude = {"roles", "warehouse", "warehouseCompany"})
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

    public User buildUserEntity() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPatronymic(patronymic);
        user.setDateOfBirth(dateOfBirth);
        user.setCity(city);
        user.setStreet(street);
        user.setHouse(house);
        user.setApartment(apartment);
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }

    public static UserDTO buildUserDTO(User user) {
        Assert.notNull(user, "User is null");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPatronymic(user.getPatronymic());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setCity(user.getCity());
        userDTO.setStreet(user.getStreet());
        userDTO.setHouse(user.getHouse());
        userDTO.setApartment(user.getApartment());
        userDTO.setEmail(user.getEmail());
        userDTO.setLogin(user.getLogin());
        userDTO.setPassword(user.getPassword());
        userDTO.setWarehouse(user.getWarehouse());
        userDTO.setWarehouseCompany(user.getWarehouseCompany());
        return userDTO;
    }
}
