package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.dao.WarehouseCustomerCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.WarehouseCustomerCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.lf5.util.StreamUtils;
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

@Service
public class WarehouseCustomerCompanyServiceImpl implements WarehouseCustomerCompanyService {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerCompanyServiceImpl.class);
    private WarehouseCustomerCompanyDAO customerCompanyDAO;

    @Autowired
    public void setDao(WarehouseCustomerCompanyDAO dao) {
        this.customerCompanyDAO = dao;
    }


    @Override
    @Transactional(readOnly = true)
    public List<WarehouseCustomerCompany> findAllWarehouseCustomerCompanies() throws DataAccessException {
        logger.info("Find all customer companies");

        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCustomerCompany.class);
        List<WarehouseCustomerCompany> companies;
        try {
            companies = customerCompanyDAO.findAll(criteria, -1, -1);
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
            Optional<WarehouseCustomerCompany> optional = customerCompanyDAO.findById(id);
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

                List<WarehouseCustomerCompany> companies = customerCompanyDAO.findAll(criteria, -1, -1);
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
            savedCompany = customerCompanyDAO.insert(company);
        } catch (GenericDAOException e) {
            logger.error("Error while saving customer company: ", e);
            throw new DataAccessException(e);
        }

        return savedCompany;
    }

    @Override
    @Transactional
    public WarehouseCustomerCompany updateWarehouseCustomerCompany(WarehouseCustomerCompany company) throws DataAccessException {
        logger.info("Update customer company: {}", company);

        WarehouseCustomerCompany updatedCompany;
        try {
            updatedCompany = customerCompanyDAO.update(company);
        } catch (GenericDAOException e) {
            logger.error("Error while updating customer company: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    public void deleteWarehouseCustomerCompany(WarehouseCustomerCompany company) throws DataAccessException {
        logger.info("Delete customer company by id #{}", company.getId());

        try {
            customerCompanyDAO.delete(company);
        } catch (GenericDAOException e) {
            logger.error("Error while deleting customer company: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany company) throws DataAccessException {
        logger.error("Determine if customer company #{} exists", company.getId());

        try {
            return customerCompanyDAO.isExistsEntity(company.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if customer company exists", e);
            throw new DataAccessException(e);
        }
    }
}
