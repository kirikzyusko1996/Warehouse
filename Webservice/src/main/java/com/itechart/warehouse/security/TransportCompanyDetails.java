package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.TransportCompany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by Aleksandr on 24.04.2017.
 */
public class TransportCompanyDetails implements UserDetails {

    private TransportCompany transportCompany;

    public TransportCompanyDetails(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
