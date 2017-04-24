package com.itechart.warehouse.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Class for authenticating user.
 */
public class WarehouseCompanyUserAuthenticationProvider implements AuthenticationProvider {
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyUserAuthenticationProvider.class);

    @Autowired
    private WarehouseCompanyUserDetailsService userDetailsService;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("Authenticating user");
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        if (userDetails != null) {
            if (DigestUtils.sha1Hex(password).equals(userDetails.getPassword()))
                return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
            else throw new BadCredentialsException("Provided incorrect password");
        } else {
            throw new BadCredentialsException("User not found");
        }
    }

    public boolean supports(Class<?> aClass) {
        return WarehouseCompanyUserDetails.class.isAssignableFrom(aClass);
    }
}
