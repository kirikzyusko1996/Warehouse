package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.StorageSpaceTypeDAO;
import com.itechart.warehouse.dto.StorageCellDTO;
import com.itechart.warehouse.dto.StorageSpaceDTO;
import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.entity.StorageSpaceType;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.StorageCellService;
import com.itechart.warehouse.service.services.StorageSpaceService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

/**
 * Created by Lenovo on 14.05.2017.
 */

@RestController
@RequestMapping(value = "/storage")
@Validated
public class StorageController {
    private StorageSpaceService storageSpaceService;
    private StorageCellService storageCellService;
    private Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    public void setStorageSpaceService(StorageSpaceService storageSpaceService) {
        this.storageSpaceService = storageSpaceService;
    }

    @Autowired
    public void setStorageCellService(StorageCellService storageCellService) {
        this.storageCellService = storageCellService;
    }

    @RequestMapping(value = "/getCellById/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<StorageCell>> findCellById(@PathVariable Long id){
        logger.info("GET on /cell with id #{}", id);

        List<StorageCell> storageCellList = new ArrayList<>();
        try{
            storageCellList.add(storageCellService.findStorageCellById(id));
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(storageCellList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<StorageSpace>> readStorageSpace(@PathVariable Long id){
        logger.info("GET on /warehouse: find all StorageSpace for {}", id);

        List<StorageSpace> storageSpaces;
        try{
            storageSpaces = storageSpaceService.findStorageByWarehouseId(id);
            System.out.println("!!!!!!!!!!"+storageSpaces);
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while reading warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(storageSpaces, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllTypeOfSpace", method = RequestMethod.GET)
    public ResponseEntity<List<StorageSpaceType>> readAllStorageSpaceType(){
        logger.info("GET on /TypeOfSpace: find all type");

        List<StorageSpaceType> storageSpaceTypes;
        try{
            storageSpaceTypes = storageSpaceService.findAllStorageSpaceType();
        } catch (DataAccessException e){
            logger.error("Error while retrieving warehouse", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(storageSpaceTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/cell/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> saveStorageCell(@RequestBody StorageCellDTO storageCellDTO){
        logger.info("POST on /Storage: save new storageCell");
        // todo security check
        try{
            System.out.println(storageCellDTO);
            storageCellService.createStorageCell(storageCellDTO);
        } catch (DataAccessException e){
            logger.error("Error while saving new storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while saving storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while saving storageCell", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/cell/save/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateStorageCell(@PathVariable String id, @RequestBody StorageCellDTO storageCellDTO){
        logger.info("PUT on /Storage: save new storageCell");
        // todo security check
        try{
            System.out.println(storageCellDTO);
            storageCellService.updateStorageCell(storageCellDTO);
        } catch (DataAccessException e){
            logger.error("Error while updating new storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/cell/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageCell(@PathVariable Long id){
        logger.info("DELETE on /Cell: disable storageCell");
        // todo security check
        try {
            System.out.println(id);
            storageCellService.deleteStorageCell(id);
        } catch (DataAccessException e){
            logger.error("Error while updating new storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> saveStorage(@RequestBody StorageSpaceDTO storageSpace){
        logger.info("POST on /Storage: save new Storage");
        // todo security check
        try{
            System.out.println(storageSpace);
            storageSpaceService.createStorageSpace(storageSpace);
        } catch (DataAccessException e){
            logger.error("Error while saving new storageSpace", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while saving storageSpace", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while saving storageSpace", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateStorage(@PathVariable String id, @RequestBody StorageSpaceDTO storageSpace){
        logger.info("PUT on /Storage: save new Storage");
        // todo security check
        try{
            storageSpaceService.updateStorageSpace(storageSpace);
        } catch (DataAccessException e){
            logger.error("Error while updating new Storage", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating storageSpace", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while updating storageSpace", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorage(@PathVariable Long id){
        logger.info("DELETE on /Storage: disable storageCell");
        // todo security check
        try {
            System.out.println(id);
            storageSpaceService.deleteStorageSpace(id);
        } catch (DataAccessException e){
            logger.error("Error while updating new storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Warehouse with specified id not found while updating storageCell", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
