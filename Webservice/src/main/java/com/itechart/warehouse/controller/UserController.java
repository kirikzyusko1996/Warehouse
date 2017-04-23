package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.UserService;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for handling requests to user service.
 */

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> doGetUsers() {
        logger.info("Handling request for list of users");
        List<User> users;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        try {
            users = userService.findUsersForCompany(userDetails.getCompany().getIdWarehouseCompany());
        } catch (DataAccessException e) {
            logger.error("Error retrieving users: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);

    }

}
