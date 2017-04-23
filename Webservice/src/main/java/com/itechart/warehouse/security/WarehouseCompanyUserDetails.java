package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

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
        //todo get authorities
        return null;
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

    public WarehouseCompany getCompany() {
        return user.getWarehouseCompany();
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
}
