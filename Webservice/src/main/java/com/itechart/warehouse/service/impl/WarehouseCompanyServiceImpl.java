package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseCompanyDAO;
import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.apache.commons.lang3.math.NumberUtils;
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
    public WarehouseCompany saveWarehouseCompany(WarehouseCompany warehouseCompany) throws DataAccessException {
        logger.info("Saving WarehouseCompany: {}", warehouseCompany);
        WarehouseCompany updatedWarehouseCompany = null;
        try {
            if (isExists(warehouseCompany)) {
                updatedWarehouseCompany = warehouseCompanyDAO.update(warehouseCompany);
            } else {
                throw new DataAccessException("Company doesn't exists");//TODO remake
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving WarehouseCompany: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return updatedWarehouseCompany;
    }

    @Override
    @Transactional
    public WarehouseCompany updateWarehouseCompany(String id, WarehouseCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update company: {}", company);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        WarehouseCompany updatedCompany;
        try {
            Long companyId = Long.valueOf(id);
            if (warehouseCompanyDAO.isExistsEntity(companyId)) {
                company.setIdWarehouseCompany(companyId);
                updatedCompany = warehouseCompanyDAO.update(company);
            } else {
                logger.error("Company with id {} not found", companyId);
                throw new ResourceNotFoundException("Company not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating company: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    public void deleteWarehouseCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting act with id: {}", id);
        if (id == null || !NumberUtils.isNumber(id)) throw new IllegalParametersException("Id is null");
        try {
            Long companyId = Long.valueOf(id);
            Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(companyId);
            if (result != null)
                warehouseCompanyDAO.delete(result.get());
        } catch (GenericDAOException e) {
            logger.error("Error during deleting act: {}", e.getMessage());
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
