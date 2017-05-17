package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.error.RequestHandlingError;
import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dto.WarehouseCustomerCompanyDTO;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
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

import static com.itechart.warehouse.util.Host.origins;

@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping("/customer")
@Validated
public class WarehouseCustomerCompanyController {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerCompanyController.class);
    private WarehouseCustomerCompanyService customerService;

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service) {
        this.customerService = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<WarehouseCustomerCompany>> readCustomers(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "-1") int count)
            throws DataAccessException, IllegalParametersException {
        logger.info("GET on /customer: find all customers");

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            List<WarehouseCustomerCompany> customers = customerService.findAllCustomersForWarehouseCompany(page, count, company.getIdWarehouseCompany());
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving customers");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<WarehouseCustomerCompany> readCustomer(@PathVariable Long id)
            throws DataAccessException, IllegalParametersException {
        logger.info("GET on /customer/{}: find customer", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            WarehouseCustomerCompany customer = customerService.findCustomerForCompanyById(id, company.getIdWarehouseCompany());
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving customer");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> saveCustomer(@Valid @RequestBody WarehouseCustomerCompanyDTO customer)
            throws DataAccessException, RequestHandlingException {
        logger.info("POST on /customer: save new customer");

        WarehouseCustomerCompany savedCustomer;

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            savedCustomer = customerService.saveWarehouseCustomerCompany(customer, company);
        } else {
            logger.error("Failed to retrieve authenticated user while saving new customer");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (savedCustomer.getId() != null){
            return new ResponseEntity<>(new IdResponse(savedCustomer.getId()), HttpStatus.CREATED);
        } else {
            throw new RequestHandlingException("Customer was not saved");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<StatusResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody WarehouseCustomerCompanyDTO customer)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /customer/{}: update customer", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            customerService.updateWarehouseCustomerCompany(id, customer, company.getIdWarehouseCompany());
        } else {
            logger.error("Failed to retrieve authenticated user while saving new customer");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<StatusResponse> deleteCustomer(@PathVariable Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("DELETE on /customer/{}: delete customer", id);

        customerService.deleteWarehouseCustomerCompany(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.DELETED), HttpStatus.OK);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public
    @ResponseBody
    RequestHandlingError handleException(DataAccessException e) {
        RequestHandlingError dataAccessError = new RequestHandlingError();
        dataAccessError.setError(e.getMessage());
        return dataAccessError;
    }

    @ExceptionHandler(IllegalParametersException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    RequestHandlingError handleException(IllegalParametersException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError illegalParametersError = new RequestHandlingError();
        illegalParametersError.setError(e.getMessage());
        return illegalParametersError;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public
    @ResponseBody
    RequestHandlingError handleException(ResourceNotFoundException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError resourceNotFoundError = new RequestHandlingError();
        resourceNotFoundError.setError(e.getMessage());
        return resourceNotFoundError;
    }
}
