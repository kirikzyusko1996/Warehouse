package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.StorageCellDAO;
import com.itechart.warehouse.dao.StorageSpaceDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.StorageCellDTO;
import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.StorageCellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public StorageCell createStorageCell(StorageCellDTO storageCellDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating storage cell from space from DTO: {}", storageCellDTO);
        if (storageCellDTO == null) throw new IllegalParametersException("storage cell DTO is null");
        try {
            StorageCell storageCell = new StorageCell();
            storageCell.setGoods(null);//TODO: maybe here will be an exception
            storageCell.setNumber(storageCellDTO.getNumber());
            storageCell.setStorageSpace(storageSpaceDAO.findById(storageCellDTO.getIdStorageSpace()).get());

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
                storageCell.setGoods(goodsDAO.findById(storageCellDTO.getIdGoods()).get());
                if(storageCell.getStorageSpace()==null || storageCell.getNumber()==null
                        || storageCell.getGoods()==null || storageCell.getIdStorageCell()==null) { //TODO: через
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

    @Override
    @Transactional
    public void deleteStorageCell(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting storage cell with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<StorageCell> result = storageCellDAO.findById(id);
            if (result.isPresent()) {
                storageCellDAO.delete(result.get());
            } else throw new ResourceNotFoundException("Storage cell with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting storage cell: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
