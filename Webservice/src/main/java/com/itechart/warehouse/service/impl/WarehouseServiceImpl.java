package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Lenovo on 25.04.2017.
 * Implementation of warehouse service.
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseDAO warehouseDAO;
    private Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    @Autowired
    public void setWarehouseDAO(WarehouseDAO warehouseDAO) {
        this.warehouseDAO = warehouseDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> findAllWarehouse() throws DataAccessException {
        logger.info("Find all warehouse");
        DetachedCriteria criteria = DetachedCriteria.forClass(Warehouse.class);
        List<Warehouse> warehouses = null;
        try {
            warehouses = warehouseDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> findWarehousesByCompanyId(String id) throws DataAccessException, IllegalParametersException {
        logger.info("Find warehouses by id company: {}", id);
        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }
        List<Warehouse> warehouses = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(Warehouse.class);
        criteria.add(Restrictions.eq("warehouseCompany.idWarehouseCompany", Long.valueOf(id)));//it's no fact, that it will work

        try {
            warehouses = warehouseDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouses;
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseById(Long id) throws DataAccessException {
        logger.info("Find warehouse by id: {}", id);
        if (id == null) return null;
        Warehouse warehouse = null;
        try {
            Optional<Warehouse> result = warehouseDAO.findById(id);
            warehouse = result.get();
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouse;
    }

    @Override
    @Transactional
    public Warehouse saveWarehouse(Warehouse warehouse) throws DataAccessException {
        logger.info("Save Warehouse: {}", warehouse);

        Warehouse savedWarehouse;
        try {
            savedWarehouse = warehouseDAO.insert(warehouse);
        } catch (GenericDAOException e) {
            logger.error("Error while saving Warehouse: ", e);
            throw new DataAccessException(e);
        }

        return savedWarehouse;
    }

    @Override
    @Transactional
    public Warehouse updateWarehouse(String id, Warehouse warehouse)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update Warehouse: {}", warehouse);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        Warehouse updatedWarehouse;
        try {
            Long warehouseId = Long.valueOf(id);
            if (warehouseDAO.isExistsEntity(warehouseId)) {
                warehouse.setIdWarehouse(warehouseId);
                updatedWarehouse = warehouseDAO.update(warehouse);
            } else {
                logger.error("Warehouse with id {} not found", warehouseId);
                throw new ResourceNotFoundException("Warehouse not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating Warehouse: ", e);
            throw new DataAccessException(e);
        }

        return updatedWarehouse;
    }

    @Override
    @Transactional
    public void deleteWarehouse(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Delete Warehouse by id #{}", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        try {
            Long warehouseId = Long.valueOf(id);
            Optional<Warehouse> optional = warehouseDAO.findById(warehouseId);
            if (optional.isPresent()) {
                Warehouse company = optional.get();
                warehouseDAO.delete(company);
            } else {
                logger.error("Warehouse with id {} not found", warehouseId);
                throw new ResourceNotFoundException("Warehouse not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting Warehouse: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(Warehouse warehouse) throws DataAccessException {
        logger.error("Determine if Warehouse #{} exists", warehouse.getIdWarehouse());

        try {
            return warehouseDAO.isExistsEntity(warehouse.getIdWarehouse());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if Warehouse exists", e);
            throw new DataAccessException(e);
        }
    }
}
