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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.itechart.warehouse.constants.UserRoleEnum.ROLE_ADMIN;
import static com.itechart.warehouse.util.Host.origins;

/**
 * Created by Lenovo on 01.05.2017.
 */
@CrossOrigin(origins = origins, maxAge = 3600)
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCompany>> readCompanies(){
        logger.info("GET on /company: find all companies");
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        User user = userDetails.getUser();//warning
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

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveCompany(@Valid @RequestBody WarehouseCompany warehouseCompany){
        logger.info("POST on /company: save new company");
        // todo security check
        try{
            warehouseCompanyService.saveWarehouseCompany(warehouseCompany);
        } catch (DataAccessException e){
            logger.error("Error while saving new company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCompany(@PathVariable String id, @Valid @RequestBody WarehouseCompany company){
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
    public ResponseEntity<?> deleteCompany(@PathVariable String id){
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
