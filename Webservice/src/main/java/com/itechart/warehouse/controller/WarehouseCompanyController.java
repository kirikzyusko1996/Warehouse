package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.itechart.warehouse.constants.UserRoleEnum.ROLE_ADMIN;
import static com.itechart.warehouse.util.Host.origins;

/**
 * Created by Lenovo on 01.05.2017.
 */

@RestController
@RequestMapping(value = "/company")
@Validated
public class WarehouseCompanyController {
    private WarehouseCompanyService warehouseCompanyService;
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyController.class);

    @Autowired
    public void setWarehouseCompanyService(WarehouseCompanyService warehouseCompanyService) {
        this.warehouseCompanyService = warehouseCompanyService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)

    public ResponseEntity<List<WarehouseCompany>> readAllCompanies(){
        logger.info("GET on /company: find all companies");
        List<WarehouseCompany> companies;
        try{
            companies = warehouseCompanyService.findAllWarehouseCompany();
        } catch (DataAccessException e){
            logger.error("Error while retrieving all companies", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCompany>> readCompanies(){
        logger.info("GET on /company: find all companies");
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        User user = userDetails.getUser();
        List<WarehouseCompany> companies;
        try{
            boolean isAdmin = userService.hasRole(user.getId(), ROLE_ADMIN);
            if(isAdmin) {
                companies = warehouseCompanyService.findAllWarehouseCompany();
            } else {
                companies = warehouseCompanyService.findWarehouseCompany(userDetails.getUser().getId());
            }
        } catch (DataAccessException e){
            logger.error("Error while retrieving all companies", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while getting company of warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("user with specified id not found while reading company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCompany>> getCompanyById(@PathVariable Long id){
        logger.info("GET on /company: by id {}", id);
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        User user = userDetails.getUser();//warning
        List<WarehouseCompany> company = new ArrayList<>();
        try{
            company.add(warehouseCompanyService.getWarehouseCompanyById(id));
        } catch (DataAccessException e){
            logger.error("Error while retrieving all companies", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while getting company of warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("user with specified id not found while reading company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/save/{email:.+}", method = RequestMethod.POST)
    public ResponseEntity<User> saveCompany(@Valid @RequestBody WarehouseCompany warehouseCompany,
                                            @PathVariable String email){
        logger.info("POST on /company: save new company");
        User user = null;
        try{
            user = warehouseCompanyService.saveWarehouseCompany(warehouseCompany, email);
            if(user == null) {
                return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);//email can't sending
            }
        } catch (DataAccessException e){
            logger.error("Error while saving new company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @Valid @RequestBody WarehouseCompany company){
        logger.info("PUT on /company/{}: update company", id);
        // todo security check
        try{
            warehouseCompanyService.updateWarehouseCompany(id, company);
        } catch (DataAccessException e){
            logger.error("Error while updating company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Company with specified id not found while updating company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompany(@PathVariable Long id){
        logger.info("DELETE on /company/{}: delete company", id);
        // todo security check
        try {
            warehouseCompanyService.deleteWarehouseCompany(id);
        } catch (DataAccessException e){
            logger.error("Error while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Transport company with specified id not found while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
