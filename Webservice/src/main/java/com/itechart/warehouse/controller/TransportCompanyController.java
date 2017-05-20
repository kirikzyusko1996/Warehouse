package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.error.RequestHandlingError;
import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dto.TransportCompanyDTO;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.TransportCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping("/tr-company")
@Validated
public class TransportCompanyController {
    private final static Logger logger = LoggerFactory.getLogger(TransportCompanyController.class);
    private TransportCompanyService transportService;

    @Autowired
    public void setTransportService(TransportCompanyService service) {
        this.transportService = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<TransportCompany>> readCompanies(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "-1") int count)
            throws DataAccessException, IllegalParametersException {
        logger.info("GET on /tr-company: find all transport companies");

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany warehouseCompany = userDetails.getCompany();
            List<TransportCompany> customers = transportService.findAllCompaniesForWarehouseCompany(page, count, warehouseCompany.getIdWarehouseCompany());
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving transport companies");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TransportCompany> readCompany(@PathVariable Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /tr-company/{}: find transport company by id", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany warehouseCompany = userDetails.getCompany();
            TransportCompany company = transportService.findTransportForCompanyById(id, warehouseCompany.getIdWarehouseCompany());
            return new ResponseEntity<>(company, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving company");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> saveCompany(@Valid @RequestBody TransportCompanyDTO transportCompany)
            throws DataAccessException, RequestHandlingException {
        logger.info("POST on /tr-company: save new transport company");

        TransportCompany savedCompany;

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            savedCompany = transportService.saveTransportCompany(transportCompany, company);
        } else {
            logger.error("Failed to retrieve authenticated user while saving new transport company");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (savedCompany.getId() != null){
            return new ResponseEntity<>(new IdResponse(savedCompany.getId()), HttpStatus.CREATED);
        } else {
            throw new RequestHandlingException("transport company was not saved");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @Valid @RequestBody TransportCompanyDTO company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /tr-company/{}: update transport company", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany warehouseCompany = userDetails.getCompany();
            transportService.updateTransportCompany(id, company, warehouseCompany.getIdWarehouseCompany());
        } else {
            logger.error("Failed to retrieve authenticated user while saving new transport company");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompany(@PathVariable Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("DELETE on /tr-company/{}: delete transport company", id);

        transportService.deleteTransportCompany(id);
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

    @ExceptionHandler(RequestHandlingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public
    @ResponseBody
    RequestHandlingError handleException(RequestHandlingException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError requestHandlingError = new RequestHandlingError();
        requestHandlingError.setError(e.getMessage());
        return requestHandlingError;
    }
}
