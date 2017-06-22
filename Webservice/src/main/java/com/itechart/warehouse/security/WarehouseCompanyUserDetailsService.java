package com.itechart.warehouse.security;

import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Class for loading user data from the database during authentication.
 */

public class WarehouseCompanyUserDetailsService implements UserDetailsService {

    private static final String ERROR_EXCEPTION_DURING_USER_RETRIEVAL = "Exception during retrieving user details from the database";

    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyUserDetailsService.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        logger.info("Loading user by login name: {}", username);
        try {
            return new WarehouseCompanyUserDetails(userService.findUserByLogin(username));
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            throw new UsernameNotFoundException(ERROR_EXCEPTION_DURING_USER_RETRIEVAL, e);
        }
    }
}
