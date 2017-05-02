package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
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

/**
 * Created by Lenovo on 01.05.2017.
 */
@RestController
@RequestMapping(value = "/company")
@Validated
public class WarehouseCompanyController {
    private WarehouseCompanyService warehouseCompanyService;
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyController.class);

    @Autowired
    public void setWarehouseCompanyService(WarehouseCompanyService warehouseCompanyService) {
        this.warehouseCompanyService = warehouseCompanyService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCompany>> readCompanies(){
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
