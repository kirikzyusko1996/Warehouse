package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
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

    @RequestMapping(value = "/{warehouseId}/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Goods>> getGoods(@PathVariable int page, @RequestParam int count, @PathVariable Long warehouseId) {
        logger.info("Handling request for list of goods in warehouse with id {}, page: {}, count: {}", warehouseId, page, count);
        List<Goods> goods = null;
        try {
            //todo security check
            goods = goodsService.findGoodsForWarehouse(warehouseId, (page - 1) * count, count);
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
    public ResponseEntity<Long> saveGoods(@PathVariable Long invoiceId, @Valid @RequestBody GoodsDTO goodsDTO) {
        logger.info("Handling request for saving new goods using DTO: {} for invoice with id {}", goodsDTO, invoiceId);
        try {
            Goods savedGoods = goodsService.createGoods(invoiceId, goodsDTO);
            return new ResponseEntity<>(savedGoods.getId(), HttpStatus.CREATED);
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

    @RequestMapping(value = "/search/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Goods>> findGoods(@PathVariable int page, @RequestParam int count, @RequestBody GoodsSearchDTO searchDTO) {
        logger.info("Handling request for searching list of goods by: {}, page: {}, count: {}", searchDTO, page, count);
        List<Goods> goods = null;
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        try {
            WarehouseCompany company = userDetails.getCompany();
            if (company != null) {
                goods = goodsService.findGoodsForWarehouseByCriteria(company.getIdWarehouseCompany(), searchDTO, (page - 1) * count, count);
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

    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST)
    public ResponseEntity<Void> setGoodsStatus(@PathVariable(value = "id") Long id, @RequestBody GoodsStatusDTO statusDTO) {
        logger.info("Handling request for setting status {} to goods with id: {}", statusDTO, id);
        //todo security check
        try {
            goodsService.setGoodsStatus(id, statusDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            logger.error("Error during setting goods status: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    //todo put in storage

    // TODO: 01.05.2017 remove from storage

    //todo save all


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
