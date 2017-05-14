package com.itechart.warehouse.controller;

import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.StorageSpaceService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

/**
 * Created by Lenovo on 14.05.2017.
 */
@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping(value = "/storage")
@Validated
public class StorageController {
    private StorageSpaceService storageSpaceService;
    private Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    public void setStorageSpaceService(StorageSpaceService storageSpaceService) {
        this.storageSpaceService = storageSpaceService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<StorageSpace>> readWarehouses(@PathVariable String id){
        System.out.println("ID: "+id);
        logger.info("GET on /warehouse: find all companies");

        List<StorageSpace> storageSpaces;
        try{
            storageSpaces = storageSpaceService.findStorageByWarehouseId(id);
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(storageSpaces, HttpStatus.OK);
    }
}
