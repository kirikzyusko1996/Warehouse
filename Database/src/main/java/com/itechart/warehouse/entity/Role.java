package com.itechart.warehouse.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Анна on 17.04.2017.
 */
@Entity
public class Role {
    private Short idRole;
    private String role;
    private List<User> users = new ArrayList<User>();

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
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        if (idRole != null ? !idRole.equals(role1.idRole) : role1.idRole != null) return false;
        if (role != null ? !role.equals(role1.role) : role1.role != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = idRole != null ? idRole.hashCode() : 0;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("idRole", idRole)
                .append("role", role)
                .toString();
    }
}
