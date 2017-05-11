package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.dao.WarehouseCustomerCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.WarehouseCustomerCompanyDTO;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import com.itechart.warehouse.service.services.WarehouseCustomerCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseCustomerCompanyServiceImpl implements WarehouseCustomerCompanyService {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerCompanyServiceImpl.class);
    private WarehouseCustomerCompanyDAO customerDAO;
    private WarehouseCompanyService companyService;

    @Autowired
    public void setCustomerDAO(WarehouseCustomerCompanyDAO dao) {
        this.customerDAO = dao;
    }

    @Autowired
    public void setCompanyService(WarehouseCompanyService service){this.companyService = service;}


    @Override
    @Transactional(readOnly = true)
    public List<WarehouseCustomerCompany> findAllWarehouseCustomerCompanies(int page, int count) throws DataAccessException {
        logger.info("Find all customer companies");

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
    //todo @PreAuthorize("hasPermission('WarehouseCustomerCompany', 'WRITE')")
    public WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompanyDTO dto, WarehouseCompany company) throws DataAccessException {
        logger.info("Save customer dto: {}", dto);

        WarehouseCustomerCompany savedCompany;
        try {
            WarehouseCustomerCompany customer = mapToEntity(dto);
            customer.setWarehouseCompany(company);

            savedCompany = customerDAO.insert(customer);
        } catch (GenericDAOException e) {
            logger.error("Error while saving customer dto: ", e);
            throw new DataAccessException(e);
        }

        return savedCompany;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'WarehouseCustomerCompany', 'UPDATE')")
    public WarehouseCustomerCompany updateWarehouseCustomerCompany(String id, WarehouseCustomerCompanyDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update customer dto: {}", dto);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        WarehouseCustomerCompany updatedCompany;
        try {
            Long customerId = Long.valueOf(id);
            if (customerDAO.isExistsEntity(customerId)) {
                dto.setId(customerId);

                WarehouseCustomerCompany company = mapToEntity(dto);
                WarehouseCompany companyOfCustomer = companyService.findWarehouseCompanyById(dto.getWarehouseCompanyId());
                company.setWarehouseCompany(companyOfCustomer);

                updatedCompany = customerDAO.update(company);
            } else {
                logger.error("Customer with id {} not found", customerId);
                throw new ResourceNotFoundException("Customer not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating customer dto: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'WarehouseCustomerCompany', 'DELETE')")
    public void deleteWarehouseCustomerCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete customer dto by: id {}", id);

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
            logger.error("Error while deleting customer dto: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany company) throws DataAccessException {
        logger.error("Determine if customer dto {} exists", company.getId());

        try {
            return customerDAO.isExistsEntity(company.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if customer dto exists", e);
            throw new DataAccessException(e);
        }
    }

    private WarehouseCustomerCompany mapToEntity(WarehouseCustomerCompanyDTO dto) {
        WarehouseCustomerCompany company = new WarehouseCustomerCompany();
        company.setId(dto.getId());
        company.setName(dto.getName());

        return company;
    }
}
