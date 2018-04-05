package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 01.05.2017.
 * Controller for request of warehouse
 */

@RestController
@RequestMapping(value = "/warehouse")
@Validated
public class WarehouseController {
    private WarehouseService warehouseService;
    private Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @Autowired
    public void setWarehouseCompanyService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @RequestMapping(value = "/getWarehouseById/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Warehouse>> findWarehousesById(@PathVariable Long id){
        logger.info("GET on /warehouse #{}: find all warehouses", id);

        List<Warehouse> warehouses = new ArrayList<>();
        try{
            warehouses.add(warehouseService.findWarehouseOfCompanyById(id));
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Warehouse>> findWarehousesByCompanyId(@RequestParam(defaultValue = "0") Long id,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "-1") int count){
        logger.info("GET on /warehouse: find warehouses");

        List<Warehouse> warehouses;
        try{
            warehouses = warehouseService.findWarehousesByCompanyId(id, page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<List<Warehouse>> searchWarehouses(@RequestBody Warehouse warehouse){
        logger.info("GET on /warehouse: search warehouse by criteria name={}", warehouse.getName());

        List<Warehouse> warehouses;
        try{
            warehouses = warehouseService.searchWarehouse(warehouse, warehouse.getWarehouseCompany().getIdWarehouseCompany());
        } catch (DataAccessException e){
            logger.error("Error reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity saveWarehouse(@Valid @RequestBody Warehouse warehouse){
        logger.info("POST on /warehouse: save new warehouse");

        try{
            warehouseService.saveWarehouse(warehouse);
        } catch (DataAccessException e){
            logger.error("Error while saving new warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateWarehouse(@PathVariable Long id, @Valid @RequestBody Warehouse warehouse){
        logger.info("PUT on /warehouse/{}: update warehouse", id);

        try{
            warehouseService.updateWarehouse(id, warehouse);
        } catch (DataAccessException e){
            logger.error("Error while updating warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while updating warehouse", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteWarehouse(@PathVariable Long id){
        logger.info("DELETE on /company/{}: delete company", id);

        try {
            warehouseService.deleteWarehouse(id);
        } catch (DataAccessException e){
            logger.error("Error while deleting Warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while deleting Warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Transport company with specified id not found while deleting Warehouse", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
