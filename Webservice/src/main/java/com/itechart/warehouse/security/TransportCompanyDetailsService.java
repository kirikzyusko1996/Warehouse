package com.itechart.warehouse.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Class for getting transport company details for implementing digest authentication.
 */
@Service
public class TransportCompanyDetailsService implements UserDetailsService {
    private Logger logger = LoggerFactory.getLogger(TransportCompanyDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
