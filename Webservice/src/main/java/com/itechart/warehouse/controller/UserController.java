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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsers() {
        logger.info("Handling request for list of registered users");
        List<User> users;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        try {
            users = userService.findUsersForCompany(userDetails.getCompany().getIdWarehouseCompany());
        } catch (DataAccessException e) {
            logger.error("Error during users retrieval: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Void> saveUser(@RequestBody User user) {
        logger.info("Handling request for saving new user with fields: {}", user);
        //todo set company id
        try {
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            logger.error("Error during user saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id") Long id, @RequestBody User user) {
        logger.info("Handling request for updating user with id: {} and fields: {}", id, user);
        //todo security check
        try {
            user.setIdUser(id);
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during user saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Long id) {
        logger.info("Handling request for deleting user with id: {}", id);
        //todo security check
        try {
            User user = userService.findUserById(id);
            userService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during user deleting: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
