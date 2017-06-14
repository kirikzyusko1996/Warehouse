package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dto.RoleDTO;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests to user service.
 */
//@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping(value = "/user")
@Validated
public class UserController {
    private static final String HEADER_X_TOTAL_COUNT = "X-total-count";
    private static final String HEADER_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEPTION_MESSAGE_COULD_NOT_RETRIEVE = "Could not retrieve authenticated user information";
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(defaultValue = "-1") int page,
                                                  @RequestParam(defaultValue = "-1") int count,
                                                  HttpServletResponse response) throws RequestHandlingException, DataAccessException, IllegalParametersException {
        logger.info("GET on /, page: {}, count: {}", page, count);
        List<UserDTO> users;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        WarehouseCompany company = userDetails.getCompany();
        if (company != null) {
            if (page == -1 && count == -1) {
                users = userService.findUsersForCompany(company.getIdWarehouseCompany(), -1, -1);
            } else {
                users = userService.findUsersForCompany(company.getIdWarehouseCompany(), (page - 1) * count, count);
            }
            long userCount = userService.getUsersCount(company.getIdWarehouseCompany());
            response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(userCount));
            response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        } else throw new RequestHandlingException(EXCEPTION_MESSAGE_COULD_NOT_RETRIEVE);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @RequestMapping(value = "/warehouse/{warehouseId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<UserDTO>> getUsersForWarehouse(@PathVariable Long warehouseId,
                                                              @RequestParam(defaultValue = "-1") int page,
                                                              @RequestParam(defaultValue = "-1") int count,
                                                              HttpServletResponse response) throws DataAccessException, IllegalParametersException {
        logger.info("GET on /warehouse/{}, page: {}, count: {}", warehouseId, page, count);
        List<UserDTO> users;
        if (page == -1 && count == -1) {
            users = userService.findUsersForWarehouse(warehouseId, -1, -1);
        } else {
            users = userService.findUsersForWarehouse(warehouseId, (page - 1) * count, count);
        }
        long userCount = userService.getUsersCountForWarehouse(warehouseId);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(userCount));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException, RequestHandlingException {
        logger.info("GET on /{}: {}", id);
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        WarehouseCompany company = userDetails.getCompany();
        UserDTO user;
        if (company != null) {
            user = userService.findUserDTOById(id);
        } else throw new RequestHandlingException(EXCEPTION_MESSAGE_COULD_NOT_RETRIEVE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RoleDTO>> getRoles() throws DataAccessException {
        logger.info("GET on /roles");
        List<RoleDTO> roles = userService.getRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<IdResponse> saveUser(@Valid @RequestBody UserDTO userDTO) throws DataAccessException, IllegalParametersException, RequestHandlingException, ResourceNotFoundException {
        logger.info("POST on /save: {}", userDTO);
        WarehouseCompany company = UserDetailsProvider.getUserDetails().getCompany();
        if (company != null) {
            Long companyId = company.getIdWarehouseCompany();
            User user = userService.saveUser(companyId, userDTO);
            if (user != null) {
                IdResponse idResponse = new IdResponse();
                idResponse.setId(user.getId());
                return new ResponseEntity<>(idResponse, HttpStatus.CREATED);
            } else throw new RequestHandlingException("User was not stored");
        } else throw new RequestHandlingException(EXCEPTION_MESSAGE_COULD_NOT_RETRIEVE);
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> updateUser(@PathVariable(value = "id") Long id,
                                                     @Valid @RequestBody UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /save/{}, DTO: {}", id, userDTO);
        userService.updateUser(id, userDTO);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/preset", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> updateUser(@RequestParam(value = "id") Long id,
                                                    @RequestParam(value = "preset") Long presetId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /preset, user id: {}, preset if: {}", id, presetId);
        userService.setSettingsPreset(id, presetId);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> deleteUser(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("DELETE on /delete/{}", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.DELETED), HttpStatus.OK);
    }


    @RequestMapping(value = "/is_occupied", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> isLoginOccupied(@RequestParam String loginName) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /is-occupied, login name: {}", loginName);
        StatusResponse resp = new StatusResponse();
        if (userService.findUserByLogin(loginName) != null) {
            resp.setStatus(StatusEnum.LOGIN_OCCUPIED);
        } else resp.setStatus(StatusEnum.LOGIN_VACANT);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
