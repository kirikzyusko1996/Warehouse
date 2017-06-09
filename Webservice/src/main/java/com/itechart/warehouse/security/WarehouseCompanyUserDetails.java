package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Details about the authenticated user.
 */
public class WarehouseCompanyUserDetails implements UserDetails {

    private User user;

    public WarehouseCompanyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(user.getRoles());
    }

    @Override
    public String getPassword() {
        if (user == null) return null;
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        if (user == null) return null;
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles == null) {
            return authorities;
        }
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public User getUser() {
        return user;
    }

    public WarehouseCompany getCompany() {
        return user.getWarehouseCompany();
    }

    public Warehouse getWarehouse() {
        return user.getWarehouse();
    }

    public Long getUserId() {
        return user.getId();
    }
}
