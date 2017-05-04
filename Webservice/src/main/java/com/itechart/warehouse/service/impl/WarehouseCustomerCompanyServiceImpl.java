package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.dao.WarehouseCustomerCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseCustomerCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class WarehouseCustomerCompanyServiceImpl implements WarehouseCustomerCompanyService {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerCompanyServiceImpl.class);
    private WarehouseCustomerCompanyDAO customerDAO;

    @Autowired
    public void setDao(WarehouseCustomerCompanyDAO dao) {
        this.customerDAO = dao;
    }


    @Override
    @Transactional(readOnly = true)
    public List<WarehouseCustomerCompany> findAllWarehouseCustomerCompanies(int page, int count) throws DataAccessException {
        logger.info("Find all customer companies");

        if (page < 0){
            page = 0;
        }

        if (count < 0){
            count = -1;
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCustomerCompany.class);
        List<WarehouseCustomerCompany> companies;
        try {
            companies = customerDAO.findAll(criteria, page, count);
        } catch (GenericDAOException e) {
            logger.error("Error while finding all customer companies: ", e);
            throw new DataAccessException(e);
        }

        return companies;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCustomerCompany findWarehouseCustomerCompanyById(Long id) throws DataAccessException {
        logger.info("Find customer company by id #{}", id);

        WarehouseCustomerCompany company = null;
        try {
            Optional<WarehouseCustomerCompany> optional = customerDAO.findById(id);
            if (optional.isPresent()) {
                company = optional.get();
            }
        } catch (GenericDAOException e) {
            logger.error("Error while finding customer company by id: ", e);
            throw new DataAccessException(e);
        }

        return company;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCustomerCompany findWarehouseCustomerCompanyByName(String name) throws DataAccessException {
        logger.info("Find customer company by name {}", name);

        WarehouseCustomerCompany company = null;

        if (StringUtils.isNotEmpty(name)) {
            try {
                DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCustomerCompany.class);
                criteria.add(Restrictions.eq("name", name));

                List<WarehouseCustomerCompany> companies = customerDAO.findAll(criteria, -1, -1);
                if (CollectionUtils.isNotEmpty(companies)) {
                    company = companies.get(0);
                }
            } catch (GenericDAOException e) {
                logger.error("Error while finding customer company by name: ", e);
                throw new DataAccessException(e);
            }
        }

        return company;
    }

    @Override
    @Transactional
    public WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompany company) throws DataAccessException {
        logger.info("Save customer company: {}", company);

        WarehouseCustomerCompany savedCompany;
        try {
            savedCompany = customerDAO.insert(company);
        } catch (GenericDAOException e) {
            logger.error("Error while saving customer company: ", e);
            throw new DataAccessException(e);
        }

        return savedCompany;
    }

    @Override
    @Transactional
    public WarehouseCustomerCompany updateWarehouseCustomerCompany(String id, WarehouseCustomerCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update customer company: {}", company);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        WarehouseCustomerCompany updatedCompany;
        try {
            Long customerId = Long.valueOf(id);
            if (customerDAO.isExistsEntity(customerId)) {
                company.setId(customerId);
                updatedCompany = customerDAO.update(company);
            } else {
                logger.error("Customer with id {} not found", customerId);
                throw new ResourceNotFoundException("Customer not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating customer company: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    public void deleteWarehouseCustomerCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete customer company by: id {}", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        try {
            Long customerId = Long.valueOf(id);
            Optional<WarehouseCustomerCompany> optional = customerDAO.findById(customerId);
            if (optional.isPresent()) {
                WarehouseCustomerCompany customer = optional.get();
                customerDAO.delete(customer);
            } else {
                logger.error("Customer with id {} not found", customerId);
                throw new ResourceNotFoundException("Customer not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting customer company: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany company) throws DataAccessException {
        logger.error("Determine if customer company {} exists", company.getId());

        try {
            return customerDAO.isExistsEntity(company.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if customer company exists", e);
            throw new DataAccessException(e);
        }
    }
}
