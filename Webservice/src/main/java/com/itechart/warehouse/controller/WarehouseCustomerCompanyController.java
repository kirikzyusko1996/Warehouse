package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseCustomerCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
@Validated
public class WarehouseCustomerCompanyController {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerCompanyController.class);
    private WarehouseCustomerCompanyService customerService;

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service){
        this.customerService = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCustomerCompany>> readCustomers(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "-1") int count){
        logger.info("GET on /customer: find all customers");

        List<WarehouseCustomerCompany> customers;
        try{
            customers = customerService.findAllWarehouseCustomerCompanies(page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all customers", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> saveCustomer(@Valid @RequestBody WarehouseCustomerCompany customer){
        logger.info("POST on /customer: save new customer");

        // todo security check

        try{
            customerService.saveWarehouseCustomerCompany(customer);
        } catch (DataAccessException e){
            logger.error("Error while saving new customer", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @Valid @RequestBody WarehouseCustomerCompany customer){
        logger.info("PUT on /customer/{}: update customer", id);

        // todo security check

        try{
            customerService.updateWarehouseCustomerCompany(id, customer);
        } catch (DataAccessException e){
            logger.error("Error while updating customer", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating customer", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Customer with specified id not found while updating customer", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCustomer(@PathVariable String id){
        logger.info("DELETE on /customer/{}: delete customer", id);

        // todo security check

        try{
            customerService.deleteWarehouseCustomerCompany(id);
        } catch (DataAccessException e){
            logger.error("Error while deleting customer", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while deleting customer", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Customer with specified id not found while deleting customer", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
