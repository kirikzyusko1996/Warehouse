package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.StorageSpaceDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.StorageSpaceService;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lenovo on 07.05.2017.
 */
@Service
public class StorageSpaceServiceImpl implements StorageSpaceService {
    private StorageSpaceDAO storageSpaceDAO;
    private WarehouseDAO warehouseDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;
    private StorageCellDAO storageCellDAO;
    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger(StorageSpaceServiceImpl.class);

    @Autowired
    public void setStorageCellDAO(StorageCellDAO storageCellDAO) {
        this.storageCellDAO = storageCellDAO;
    }

    @Autowired
    public void setStorageSpaceDAO(StorageSpaceDAO storageSpaceDAO) {
        this.storageSpaceDAO = storageSpaceDAO;
    }

    @Autowired
    public void setWarehouseDAO(WarehouseDAO warehouseDAO) {
        this.warehouseDAO = warehouseDAO;
    }

    @Autowired
    public void setStorageSpaceTypeDAO(StorageSpaceTypeDAO storageSpaceTypeDAO) {
        this.storageSpaceTypeDAO = storageSpaceTypeDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageSpace> findStorageByWarehouseId(String id) throws DataAccessException, IllegalParametersException {
        logger.info("Find storage by id warehouse: {}", id);
        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }
        List<StorageSpace> storageSpaces = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpace.class);
        criteria.add(Restrictions.eq("warehouse.idWarehouse", Long.valueOf(id)));//it's no fact, that it will work

        try {
            storageSpaces = storageSpaceDAO.findAll(criteria, -1, -1);

        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return storageSpaces;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageSpaceType> findAllStorageSpaceType() throws DataAccessException {
        logger.info("Find all storage space type");

        List<StorageSpaceType> storageSpaceTypes = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);

        try {
            storageSpaceTypes = storageSpaceTypeDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return storageSpaceTypes;
    }

    @Override
    @Transactional
    public StorageSpace createStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating storage space from DTO: {}", storageSpaceDTO);
        if (storageSpaceDTO == null) throw new IllegalParametersException("storage space DTO is null");
        try {
            StorageSpace storageSpace = new StorageSpace();
            storageSpace.setStorageCellList(new ArrayList<>());//so we are creating only place
            storageSpace.setWarehouse(warehouseDAO.findById(storageSpaceDTO.getIdWarehouse()).get());
            storageSpace.setStorageSpaceType(storageSpaceTypeDAO.findById(storageSpaceDTO.getIdStorageSpaceType()).get());

            if (storageSpace.getWarehouse() != null && storageSpace.getStorageSpaceType() != null ) {
                storageSpace = storageSpaceDAO.insert(storageSpace);
                return storageSpace;
            } else {
                throw new ResourceNotFoundException("Such data was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving storage space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public StorageSpace updateStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating storage space with id {} from DTO: {}", storageSpaceDTO.getIdStorageSpace(), storageSpaceDTO);
        if (storageSpaceDTO == null || storageSpaceDTO.getIdStorageSpace() == null) throw new IllegalParametersException("Id or act DTO is null");
        try {
            Optional<StorageSpace> storageSpaceResult = storageSpaceDAO.findById(storageSpaceDTO.getIdStorageSpace());
            if (storageSpaceResult.isPresent()) {
                StorageSpace storageSpace = storageSpaceResult.get();
                Warehouse warehouse = warehouseDAO.findById(storageSpaceDTO.getIdWarehouse()).get();
                StorageSpaceType storageSpaceType = storageSpaceTypeDAO.findById(storageSpaceDTO.getIdStorageSpaceType()).get();
                Long idStorageSpace = storageSpaceDTO.getIdStorageSpace();

                if(warehouse == null || storageSpaceType == null
                        || idStorageSpace==null) { //TODO: додедать проверку ячеек
                    throw new IllegalParametersException("Can't find such data in the database");
                }
                else {
                    storageSpace.setWarehouse(warehouse);
                    storageSpace.setStorageSpaceType(storageSpaceType);
                    storageSpace.setIdStorageSpace(idStorageSpace);
                    storageSpace = storageSpaceDAO.update(storageSpace);
                    return storageSpace;
                }
            } else {
                throw new ResourceNotFoundException("Storage Space with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving Storage Space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteStorageSpace(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting storage space with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<StorageSpace> result = storageSpaceDAO.findById(id);
            if (result.isPresent()) {
                storageSpaceDAO.delete(result.get());
            } else throw new ResourceNotFoundException("Storage space with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting storage space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
