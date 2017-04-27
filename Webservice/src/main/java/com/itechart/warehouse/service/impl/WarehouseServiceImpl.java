package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.WarehouseService;
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
    public List<Warehouse> findWarehousesByCompany(Long id) throws DataAccessException {
        logger.info("Find warehouses by id company: {}", id);
        if (id == null) return null;
        List<Warehouse> warehouses = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(Warehouse.class);
        criteria.add(Restrictions.eq("warehouseCompany", id));//it's no fact, that it will work

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
}
