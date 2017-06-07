package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.StorageCellDAO;
import com.itechart.warehouse.dao.StorageSpaceDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.SchemeDTO;
import com.itechart.warehouse.dto.StorageCellDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.StorageCellService;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lenovo on 07.05.2017.
 */
@Service
public class StorageCellServiceImpl implements StorageCellService {
    private StorageCellDAO storageCellDAO;
    private StorageSpaceDAO storageSpaceDAO;
    private GoodsDAO goodsDAO;
    private Logger logger = LoggerFactory.getLogger(StorageCellServiceImpl.class);

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Autowired
    public void setStorageCellDAO(StorageCellDAO storageCellDAO) {
        this.storageCellDAO = storageCellDAO;
    }

    @Autowired
    public void setStorageSpaceDAO(StorageSpaceDAO storageSpaceDAO) {
        this.storageSpaceDAO = storageSpaceDAO;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#storageCellDTO.idStorageSpace, 'Space', 'GET')")
    public StorageCell createStorageCell(StorageCellDTO storageCellDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating storage cell from space from DTO: {}", storageCellDTO);
        if (storageCellDTO == null) throw new IllegalParametersException("storage cell DTO is null");
        try {
            StorageCell storageCell = new StorageCell();
            storageCell.setGoods(null);//TODO: maybe here will be an exception
            storageCell.setNumber(storageCellDTO.getNumber());
            storageCell.setStorageSpace(storageSpaceDAO.findById(storageCellDTO.getIdStorageSpace()).get());
            storageCell.setStatus(storageCellDTO.getStatus());

            if (storageCell.getNumber()!=null && storageCell.getStorageSpace()!=null) {
                storageCell = storageCellDAO.insert(storageCell);
                return storageCell;
            } else {
                throw new ResourceNotFoundException("Such data was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving storage cell: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#storageCellDTO.idStorageCell, 'Cell', 'GET')")
    public StorageCell updateStorageCell(StorageCellDTO storageCellDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating storage cell with id {} from DTO: {}", storageCellDTO.getIdStorageSpace(), storageCellDTO);
        if (storageCellDTO == null || storageCellDTO.getIdStorageCell() == null) throw new IllegalParametersException("Id or act DTO is null");
        try {
            Optional<StorageCell> storageCellResult = storageCellDAO.findById(storageCellDTO.getIdStorageCell());
            if (storageCellResult.isPresent()) {
                StorageCell storageCell = storageCellResult.get();
                storageCell.setStorageSpace(storageSpaceDAO.findById(storageCellDTO.getIdStorageSpace()).get());
                storageCell.setNumber(storageCellDTO.getNumber());
                storageCell.setIdStorageCell(storageCellDTO.getIdStorageCell());
                storageCell.setGoods(storageCell.getGoods());//because this parameter immutable to front end
                storageCell.setStatus(storageCellDTO.getStatus());

                if(storageCell.getStorageSpace()==null || storageCell.getNumber()==null
                         || storageCell.getIdStorageCell()==null) {
                    throw new IllegalParametersException("Can't find such data in the database");
                }
                else {
                    storageCell = storageCellDAO.update(storageCell);
                    return storageCell;
                }
            } else {
                throw new ResourceNotFoundException("Storage Cell with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving Storage cell: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    /**
     * Because this method don't delete really in the database
     * and merely change status, this method can call twice:
     * when you "delete" entity and "restore" entity,
     * so this method just change status to opposite
     * */
    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Cell', 'GET')")
    public void deleteStorageCell(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting storage cell with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<StorageCell> result = storageCellDAO.findById(id);
            if (result.isPresent()) {
                result.get().setStatus(!result.get().getStatus());//so can recovery it, merely change status to opposite
                storageCellDAO.update(result.get());
            } else throw new ResourceNotFoundException("Storage cell with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting storage cell: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'Cell', 'GET')")
    public StorageCell findStorageCellById(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Find StorageCell by id: {}", id);

        StorageCell storageCell = null;
        try {
            Optional<StorageCell> result = storageCellDAO.findById(id);
            storageCell = result.get();
            System.out.println(storageCell);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return storageCell;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyByCell(Long id_cell) throws DataAccessException {
        logger.info("Find warehouse company by id of cell: {}", id_cell);

        WarehouseCompany warehouseCompany = null;
        try {
            warehouseCompany = storageCellDAO.findWarehouseCompanyByCell(id_cell);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouseCompany;
    }
}
