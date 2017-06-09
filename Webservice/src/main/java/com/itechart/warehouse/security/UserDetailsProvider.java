package com.itechart.warehouse.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Retrieves authenticated user details from the security context.
 */
public class UserDetailsProvider {

    private static Logger logger = LoggerFactory.getLogger(UserDetailsProvider.class);

    private UserDetailsProvider() {
    }

    public static WarehouseCompanyUserDetails getUserDetails() {
        logger.info("Getting user details from the security context");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return (WarehouseCompanyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;

    }
}
