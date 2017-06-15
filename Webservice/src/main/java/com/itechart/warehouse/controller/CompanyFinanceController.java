package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.CompanyPriceListDTO;
import com.itechart.warehouse.entity.CompanyPriceList;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.CompanyFinanceService;
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
@RequestMapping(value = "/finance/company")
@Validated
public class CompanyFinanceController {
    private CompanyFinanceService companyFinanceService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public void setFinanceService(CompanyFinanceService companyFinanceService) {
        this.companyFinanceService = companyFinanceService;
    }

    @RequestMapping(value = "/price", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyPriceList>> getAllPrices(){
        logger.info("getAllPrices");
        List<CompanyPriceList> priceList;
        try{
            priceList = companyFinanceService.findAllPrices(0, 0);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all prices for companies: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/newPrice", method = RequestMethod.POST)
    public ResponseEntity<Void> updatePrice(@Valid @RequestBody CompanyPriceListDTO priceDTO) {
        logger.info("Handling request for creating/updating price: {}", priceDTO);
        try {
            companyFinanceService.newPrice(priceDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GenericDAOException e) {
            logger.error("Error during creating/updating price {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/price/warehouseCompany/{idWarehouseCompany}", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyPriceList>> getPricesForWarehouseCompany(@PathVariable(value = "idWarehouseCompany") Long id){
        logger.info("getPricesForWarehouseCompany, id: {}", id);
        List<CompanyPriceList> priceList;
        try{
            priceList = companyFinanceService.findPricesForWarehouseCompany(id, 0, 0);
        } catch (DataAccessException e){
            logger.error("Error while retrieving prices: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/currentPrices", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyPriceList>> getCurrentPrices(){
        logger.info("Getting current prices");
        List<CompanyPriceList> priceList;
        try{
            priceList = companyFinanceService.findCurrentPrices();
        } catch (DataAccessException e) {
            logger.error("Error while retrieving prices: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(priceList, HttpStatus.OK);
    }
}
