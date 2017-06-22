package com.itechart.warehouse.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Анна on 17.04.2017.
 */
@Entity
@ToString(exclude = "users")
@EqualsAndHashCode(exclude = "users")
public class Role {
    private Short idRole;
    private String name;
    private List<User> users = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    public Short getIdRole() {
        return idRole;
    }

    public void setIdRole(Short idRole) {
        this.idRole = idRole;
    }

    @Basic
    @Column(name = "name", unique = true, length = 20)
    public String getName() {
        return name;
    }

    public void setName(String role) {
        this.name = role;
    }


    public void setUsers(List<User> users) {
        this.users = users;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "id_role")},
            inverseJoinColumns = {@JoinColumn(name = "id_user")})
    public List<User> getUsers() {
        return users;
    }

}
