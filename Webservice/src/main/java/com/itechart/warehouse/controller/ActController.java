package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.ActService;
import com.itechart.warehouse.validation.ValidationError;
import com.itechart.warehouse.validation.ValidationErrorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests to goods service.
 */

@RestController
@RequestMapping(value = "/act")
@Validated
public class ActController {
    private ActService actService;
    private Logger logger = LoggerFactory.getLogger(ActController.class);

    @Autowired
    public void setActService(ActService actService) {
        this.actService = actService;
    }

    @RequestMapping(value = "/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Act>> getUsers(@PathVariable int page, @RequestParam int count) {
        logger.info("Handling request for list of acts, page: {}, count: {}", page, count);
        List<Act> acts = null;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        try {
            WarehouseCompany company = userDetails.getCompany();
            if (company != null) {
                acts = actService.findActsForCompany(company.getIdWarehouseCompany(), (page - 1) * count, count);
            } else return new ResponseEntity<>(acts, HttpStatus.CONFLICT);
        } catch (DataAccessException e) {
            logger.error("Error during acts retrieval: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(acts, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Void> saveUser(@Valid @RequestBody ActDTO actDTO) {
        logger.info("Handling request for saving new act using DTO: {}", actDTO);
        try {
            //todo set goods etc.
            actService.createAct(actDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            logger.error("Error during act saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id") Long id, @Valid @RequestBody ActDTO actDTO) {
        logger.info("Handling request for updating act with id: {} by DTO: {}", id, actDTO);
        //todo security check
        try {
            actService.updateAct(id, actDTO);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during act saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Long id) {
        logger.info("Handling request for deleting act with id: {}", id);
        //todo security check
        try {
            actService.deleteAct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during act deleting: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    ValidationError handleException(MethodArgumentNotValidException e) {
        return createValidationError(e);
    }


    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }
}
