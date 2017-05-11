package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.TransportCompanyDTO;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.TransportCompanyService;
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
@RequestMapping("/tr-company")
@Validated
public class TransportCompanyController {
    private final static Logger logger = LoggerFactory.getLogger(TransportCompanyController.class);
    private TransportCompanyService transportService;

    @Autowired
    public void setTransportService(TransportCompanyService service){
        this.transportService = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<TransportCompany>> readCompanies(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "-1") int count){
        logger.info("GET on /tr-company: find all transport companies");

        List<TransportCompany> companies;
        try{
            companies = transportService.findAllTransportCompanies(page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all customers", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TransportCompany> readCompanyById(@PathVariable String id){
        logger.info("GET on /tr-company/{}: find transport company by id");

        TransportCompany company;
        try{
            company = transportService.findTransportCompanyById(id);
        } catch (DataAccessException e){
            logger.error("Error while retrieving transport company by id", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while transport company by id", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("transport company with specified id not found while transport company by id", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> saveCompany(@Valid @RequestBody TransportCompanyDTO transportCompany){
        logger.info("POST on /tr-company: save new transport company");

        try{
            WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
            if (userDetails != null) {
                WarehouseCompany company = userDetails.getCompany();
                transportService.saveTransportCompany(transportCompany, company);
            }
        } catch (DataAccessException e){
            logger.error("Error while saving new transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCompany(@PathVariable String id, @Valid @RequestBody TransportCompanyDTO company){
        logger.info("PUT on /tr-company/{}: update transport company", id);

        try{
            transportService.updateTransportCompany(id, company);
        } catch (DataAccessException e){
            logger.error("Error while updating transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("transport company with specified id not found while updating transport company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompany(@PathVariable String id){
        logger.info("DELETE on /tr-company/{}: delete transport company", id);

        try{
            transportService.deleteTransportCompany(id);
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
