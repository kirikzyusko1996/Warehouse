package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Date;
import java.util.List;

/**
 * Data transfer object for user entity with all fields: including login and password.
 */
@Setter
@Getter
@lombok.ToString(exclude = "roles")
public class UserDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String firstName;
    @NotEmpty(message = "Last name can not be empty")
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String lastName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String patronymic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
    private List<String> roles;

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
}
