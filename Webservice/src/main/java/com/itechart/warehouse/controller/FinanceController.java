package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.deserializer.LocalDateJsonDeserializer;
import com.itechart.warehouse.dto.PriceListDTO;
import com.itechart.warehouse.entity.PriceList;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.FinanceService;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping(value = "/finance")
@Validated
public class FinanceController {
    private FinanceService financeService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public void setFinanceService(FinanceService financeService) {
        this.financeService = financeService;
    }

    @RequestMapping(value = "/price", method = RequestMethod.GET)
    public ResponseEntity<List<PriceList>> getAllPrices(){
        logger.info("getAllPrices");
        List<PriceList> priceList;
        try{
            priceList = financeService.findAllPrices(0, 0);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all prices: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/price/storageType/{storageType}", method = RequestMethod.GET)
    public ResponseEntity<List<PriceList>> getPricesForStorageSpaceType(@PathVariable(value = "storageType") Short id){
        logger.info("getPricesForStorageSpaceType");
        List<PriceList> priceList;
        try{
            priceList = financeService.findPricesForStorageSpaceType(id, 0, 0);
        } catch (DataAccessException e){
            logger.error("Error while retrieving prices: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/date_price", method = RequestMethod.GET)
    public ResponseEntity<List<PriceList>> getPricesByDate(
            @NotEmpty @RequestParam("dateStart") String startDate, @NotEmpty @RequestParam("dateEnd") String endDate,
            @NotEmpty @RequestParam("storageSpaceTypeId") Short idStorageSpaceType){
        logger.info("getPricesByDate for storageSpaceTypeId: {} from {} to {}", idStorageSpaceType, startDate, endDate);
        List<PriceList> priceList;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            priceList = financeService.findPricesByDate(idStorageSpaceType,
                     new LocalDate(format.parse(startDate).getTime()),
                    new LocalDate(format.parse(endDate).getTime()), 0, 0);
        } catch (DataAccessException e){
            logger.error("Error while retrieving prices: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Illegal parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/newPrice", method = RequestMethod.POST)
    public ResponseEntity<Void> updateUser(@Valid @RequestBody PriceListDTO priceDTO) {
        logger.info("Handling request for creating/updating price: {}", priceDTO);
        try {
            financeService.newPrice(priceDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GenericDAOException e) {
            logger.error("Error during creating/updating price {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
