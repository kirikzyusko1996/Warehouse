package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseCompanyDAO;
import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Lenovo on 25.04.2017.
 */
@Service
public class WarehouseCompanyServiceImpl implements WarehouseCompanyService {
    private WarehouseCompanyDAO warehouseCompanyDAO;
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyServiceImpl.class);

    @Autowired
    public void setWarehouseCompanyDAO(WarehouseCompanyDAO warehouseCompanyDAO) {
        this.warehouseCompanyDAO = warehouseCompanyDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseCompany> findAllWarehouseCompany() throws DataAccessException {
        logger.info("Find all warehouse companies");
        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCompany.class);
        List<WarehouseCompany> warehouseCompanies = null;
        try {
            warehouseCompanies = warehouseCompanyDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse companies: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouseCompanies;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyById(Long id) throws DataAccessException{
        logger.info("Find warehouse by id: {}", id);
        if (id == null) return null;
        WarehouseCompany warehouseCompany = null;
        try {
            Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(id);
            warehouseCompany = result.get();
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouseCompany;
    }

    @Override
    @Transactional
    public WarehouseCompany saveWarehouse(WarehouseCompany warehouseCompany) throws DataAccessException {
        logger.info("Saving WarehouseCompany: {}", warehouseCompany);
        WarehouseCompany updatedWarehouseCompany = null;
        try {
            if (isExists(warehouseCompany)) {
                updatedWarehouseCompany = warehouseCompanyDAO.update(warehouseCompany);
            } else {
                updatedWarehouseCompany = warehouseCompanyDAO.insert(warehouseCompany);
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving WarehouseCompany: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return updatedWarehouseCompany;
    }

    @Override
    @Transactional
    public void deleteWarehouse(WarehouseCompany warehouseCompany) throws DataAccessException {
        logger.info("Deleting Warehouse Company: {}", warehouseCompany);
        try {
            warehouseCompanyDAO.delete(warehouseCompany);
        } catch (GenericDAOException e) {
            logger.error("Error during deleting Warehouse Company: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(WarehouseCompany warehouseCompany) throws DataAccessException {
        try {
            return warehouseCompanyDAO.isExistsEntity(warehouseCompany.getIdWarehouseCompany());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if WarehouseCompany with id: {} exists", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
