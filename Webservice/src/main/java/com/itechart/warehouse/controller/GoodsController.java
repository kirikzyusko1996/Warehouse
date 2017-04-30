package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.GoodsService;
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
@RequestMapping(value = "/goods")
@Validated
public class GoodsController {
    private GoodsService goodsService;
    private Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @RequestMapping(value = "/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Goods>> getGoods(@PathVariable int page, @RequestParam int count) {
        logger.info("Handling request for list of goods, page: {}, count: {}", page, count);
        List<Goods> goods = null;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        try {
            WarehouseCompany company = userDetails.getCompany();
            if (company != null) {
                goods = goodsService.findGoodsForCompany(company.getIdWarehouseCompany(), (page - 1) * count, count);
            } else return new ResponseEntity<>(goods, HttpStatus.CONFLICT);
        } catch (DataAccessException e) {
            logger.error("Error during goods retrieval: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/{invoiceId}/save", method = RequestMethod.POST)
    public ResponseEntity<Void> saveGoods(@PathVariable Long invoiceId, @Valid @RequestBody GoodsDTO goodsDTO) {
        logger.info("Handling request for saving new goods using DTO: {}", goodsDTO);
        try {
            //todo set invoice etc.
            goodsService.createGoods(invoiceId, goodsDTO);
            //todo save status
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            logger.error("Error during goods saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateGoods(@PathVariable(value = "id") Long id, @Valid @RequestBody GoodsDTO goodsDTO) {
        logger.info("Handling request for updating goods with id: {} by DTO: {}", id, goodsDTO);
        //todo security check
        try {
            goodsService.updateGoods(id, goodsDTO);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during goods saving: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGoods(@PathVariable(value = "id") Long id) {
        logger.info("Handling request for deleting user with id: {}", id);
        //todo security check
        try {
            goodsService.deleteGoods(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            logger.error("Error during goods deleting: {}", e.getMessage());
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
