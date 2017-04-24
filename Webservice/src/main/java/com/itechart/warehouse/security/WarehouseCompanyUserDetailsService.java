package com.itechart.warehouse.security;

import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Class for loading user data from the database during authentication.
 */

@Service
public class WarehouseCompanyUserDetailsService implements UserDetailsService {
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyUserDetailsService.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by login name: {}", username);
        try {
            //todo get warehouse company for user
            return new WarehouseCompanyUserDetails(userService.findUserByLogin(username));
        } catch (DataAccessException e) {
            throw new UsernameNotFoundException("Exception during retrieving user details from the database", e);
        }

    }
}
