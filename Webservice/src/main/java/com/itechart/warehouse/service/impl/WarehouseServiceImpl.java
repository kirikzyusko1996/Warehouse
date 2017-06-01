package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseCompanyDAO;
import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lenovo on 25.04.2017.
 * Implementation of warehouse service.
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseDAO warehouseDAO;
    private WarehouseCompanyDAO warehouseCompanyDAO;
    private Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    @Autowired
    public void setWarehouseDAO(WarehouseDAO warehouseDAO) {
        this.warehouseDAO = warehouseDAO;
    }

    @Autowired
    public void setWarehouseCompanyDAO(WarehouseCompanyDAO warehouseCompanyDAO) {
        this.warehouseCompanyDAO = warehouseCompanyDAO;
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
    public List<Warehouse> findWarehousesByCompanyId(String id, int page, int count) throws DataAccessException, IllegalParametersException {
        logger.info("Find warehouses by id company: {}", id);
        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }
        List<Warehouse> warehouses = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(Warehouse.class);
        criteria.add(Restrictions.eq("warehouseCompany.idWarehouseCompany", Long.valueOf(id)));//it's no fact, that it will work

        try {
            warehouses = warehouseDAO.findAll(criteria, page, count);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouses;
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseById(String id) throws DataAccessException, IllegalParametersException {
        logger.info("Find warehouse by id: {}", id);
        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }
        Warehouse warehouse = null;
        try {
            Long id_warehouse = Long.valueOf(id);
            Optional<Warehouse> result = warehouseDAO.findById(id_warehouse);
            warehouse = result.get();
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouse;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> searchWarehouse(Warehouse searchWarehouse, Long id_company)
            throws DataAccessException, IllegalParametersException {
        logger.info("Find warehouses with criteria by id company: {}", id_company);
        if (id_company == null) {
            throw new IllegalParametersException("Invalid id param");
        }
        List<Warehouse> warehouses = null;
        List<Warehouse> result = new ArrayList<>();
        DetachedCriteria criteria = DetachedCriteria.forClass(Warehouse.class);
        criteria.add(Restrictions.eq("warehouseCompany.idWarehouseCompany", Long.valueOf(id_company)));

        try {
            warehouses = warehouseDAO.findAll(criteria, -1, -1);
            for(Warehouse warehouse: warehouses) {
                System.out.println("From cycle: "+warehouse);
                if(warehouse.getName()!=null &&
                        warehouse.getName().toLowerCase().contains(searchWarehouse.getName().toLowerCase())) {
                    result.add(warehouse);
                }
            }
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return result;
    }

    @Override
    @Transactional
    public Warehouse saveWarehouse(Warehouse warehouse) throws DataAccessException {
        logger.info("Save Warehouse: {}", warehouse);

        Warehouse savedWarehouse;
        try {
            Optional<WarehouseCompany> optional = warehouseCompanyDAO.findById(warehouse.getWarehouseCompany().getIdWarehouseCompany());
            warehouse.setWarehouseCompany(optional.get());
            System.out.println("nEW OBJECT: "+warehouse);
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
                warehouse.setWarehouseCompany(warehouseCompanyDAO.findById(warehouse.getWarehouseCompany().getIdWarehouseCompany()).get());
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
                company.setStatus(false);
                warehouseDAO.update(company);
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
