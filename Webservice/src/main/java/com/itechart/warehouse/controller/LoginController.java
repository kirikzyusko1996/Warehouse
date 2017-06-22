package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller which is emulates authentication process (authentication itself handled by Spring Security framework).
 * It's only method handles requests to "/login" url and returns authenticated user DTO.
 */
@RestController
@RequestMapping(value = "/login")
public class LoginController {
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDTO> getUser() throws DataAccessException, IllegalParametersException, ResourceNotFoundException, RequestHandlingException {
        logger.info("Handling login request");
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        UserDTO user = null;
        if (userDetails.getUser() != null) {
            user = userService.findUserDTOById(userDetails.getUser().getId());
        } else throw new RequestHandlingException("Could not retrieve authenticated user information");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
