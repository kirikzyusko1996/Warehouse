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
    public void setCompanyService(WarehouseCompanyService service) {
        this.companyService = service;
    }


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
    @PreAuthorize("hasPermission(#companyId, 'WarehouseCompany', 'GET')")
    public List<WarehouseCustomerCompany> findAllCustomersForWarehouseCompany(int page, int count, Long companyId)
            throws DataAccessException, IllegalParametersException {
        logger.info("Find all customer companies");

        if (companyId == null) {
            throw new IllegalParametersException("Company id is null");
        }

        List<WarehouseCustomerCompany> customers;
        try {
            customers = customerDAO.findCustomersByWarehouseCompanyId(companyId, page, count);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for customers: {}", e.getMessage());
            throw new DataAccessException(e);
        }

        return customers;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCustomerCompany findCustomerById(Long id) throws DataAccessException {
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
    @PreAuthorize("hasPermission(#id, 'WarehouseCustomerCompany', 'GET')")
    public WarehouseCustomerCompany findCustomerForCompanyById(Long id, Long warehouseCompanyId) throws DataAccessException {
        return findCustomerById(id);
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
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyByCustomerId(Long customerId)
            throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse company of customer with id {}", customerId);

        if (customerId == null) {
            throw new IllegalParametersException("Customer id is null");
        }

        WarehouseCustomerCompany customer = findCustomerById(customerId);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with such id was not found");
        }

        return customer.getWarehouseCompany();
    }

    @Override
    @Transactional
    //todo @PreAuthorize("hasPermission('WarehouseCustomerCompany', 'WRITE')")
    public WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompanyDTO dto, WarehouseCompany company) throws DataAccessException {
        logger.info("Save customer dto: {}", dto);

        WarehouseCustomerCompany savedCompany;
        try {
            WarehouseCustomerCompany customer = mapToCustomer(dto);
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
    public WarehouseCustomerCompany updateWarehouseCustomerCompany(Long id, WarehouseCustomerCompanyDTO dto, Long companyId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update customer dto: {}", dto);

        WarehouseCustomerCompany updatedCompany;
        try {
            dto.setId(id);

                WarehouseCustomerCompany company = mapToCustomer(dto);
                WarehouseCompany companyOfCustomer = companyService.findWarehouseCompanyById(companyId);
                company.setWarehouseCompany(companyOfCustomer);

            updatedCompany = customerDAO.update(company);
        } catch (GenericDAOException e) {
            logger.error("Error while updating customer dto: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'WarehouseCustomerCompany', 'DELETE')")
    public void deleteWarehouseCustomerCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete customer dto by: id {}", id);

        try {
            Optional<WarehouseCustomerCompany> optional = customerDAO.findById(id);
            if (optional.isPresent()) {
                WarehouseCustomerCompany customer = optional.get();
                customerDAO.delete(customer);
            } else {
                logger.error("Customer with id {} not found", id);
                throw new ResourceNotFoundException("Customer not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting customer dto: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#warehouseCompany.idWarehouseCompany, 'WarehouseCompany', 'GET')")
    public List<WarehouseCustomerCompany> searchSimilarToCompanyForWarehouseCompany(WarehouseCustomerCompanyDTO customer, WarehouseCompany warehouseCompany) throws DataAccessException, IllegalParametersException {
//        ElasticSearchTransportCompany searchTransportCompany = new ElasticSearchTransportCompany();
//        TransportCompany company = mapToCompany(dto);
//        company.setWarehouseCompany(warehouseCompany);
//
//        List<SimilarityWrapper<TransportCompany>> companiesByRelevance = searchTransportCompany.search(company);
//        return companiesByRelevance.stream().map(SimilarityWrapper::getOjbect).collect(Collectors.toList());

        // TODO: wait until elastic will be added
        return findAllCustomersForWarehouseCompany(-1, -1, warehouseCompany.getIdWarehouseCompany());
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

    @Override
    public WarehouseCustomerCompany mapToCustomer(WarehouseCustomerCompanyDTO dto) {
        WarehouseCustomerCompany company = new WarehouseCustomerCompany();
        company.setId(dto.getId());
        company.setName(dto.getName());

        return company;
    }

    @Override
    public WarehouseCustomerCompanyDTO mapToDto(WarehouseCustomerCompany customer) {
        WarehouseCustomerCompanyDTO dto = new WarehouseCustomerCompanyDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setWarehouseCompanyId(customer.getWarehouseCompany().getIdWarehouseCompany());

        return dto;
    }
}
