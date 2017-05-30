package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.DriverDAO;
import com.itechart.warehouse.dao.TransportCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.TransportCompanyDTO;
import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.elasticsearch.ElasticSearchDriver;
import com.itechart.warehouse.service.elasticsearch.ElasticSearchTransportCompany;
import com.itechart.warehouse.service.elasticsearch.SimilarityWrapper;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.TransportCompanyService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransportCompanyServiceImpl implements TransportCompanyService{
    private final static Logger logger = LoggerFactory.getLogger(TransportCompanyServiceImpl.class);
    private TransportCompanyDAO transportDAO;
    private WarehouseCompanyService companyService;

    @Autowired
    public void setDao(TransportCompanyDAO dao) {
        this.transportDAO = dao;
    }

    @Autowired
    public void setCompanyService(WarehouseCompanyService service){this.companyService = service;}

    @Override
    @Transactional(readOnly = true)
    public List<TransportCompany> findAllTransportCompanies(int page, int count) throws DataAccessException {
        logger.info("Find all transport companies");

        DetachedCriteria criteria = DetachedCriteria.forClass(TransportCompany.class);
        List<TransportCompany> companies;
        try {
            companies = transportDAO.findAll(criteria, page, count);
        } catch (GenericDAOException e) {
            logger.error("Error while finding all transport companies: ", e);
            throw new DataAccessException(e);
        }

        return companies;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseCompanyId, 'WarehouseCompany', 'GET')")
    public List<TransportCompany> findAllCompaniesForWarehouseCompany(int page, int count, Long warehouseCompanyId)
            throws DataAccessException, IllegalParametersException {
        logger.info("Find all transport companies");

        if (warehouseCompanyId == null) {
            throw new IllegalParametersException("Company id is null");
        }

        List<TransportCompany> companies;
        try {
            companies = transportDAO.findCustomersByWarehouseCompanyId(warehouseCompanyId, page, count);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for customers: {}", e.getMessage());
            throw new DataAccessException(e);
        }

        return companies;
    }

    @Override
    @Transactional(readOnly = true)
    public TransportCompany findTransportCompanyById(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Find transport dto by id #{}", id);

        TransportCompany company = null;
        try {
            if (transportDAO.isExistsEntity(id)) {
                Optional<TransportCompany> optional = transportDAO.findById(id);
                if (optional.isPresent()){
                    company = optional.get();
                }
            } else {
                logger.error("Transport dto with id {} not found", id);
                throw new ResourceNotFoundException("transport dto not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while finding transport dto by id: ", e);
            throw new DataAccessException(e);
        }

        return company;
    }

    @Override
    @Transactional(readOnly = true)
    public TransportCompany findTransportCompanyByName(String name) throws DataAccessException {
        logger.info("Find transport dto by name {}", name);

        TransportCompany company = null;

        if (StringUtils.isNotEmpty(name)) {
            try {
                DetachedCriteria criteria = DetachedCriteria.forClass(TransportCompany.class);
                criteria.add(Restrictions.eq("name", name));

                List<TransportCompany> companies = transportDAO.findAll(criteria, -1, -1);
                if (CollectionUtils.isNotEmpty(companies)) {
                    company = companies.get(0);
                }
            } catch (GenericDAOException e) {
                logger.error("Error while finding transport dto by name: ", e);
                throw new DataAccessException(e);
            }
        }

        return company;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseCompanyId, 'WarehouseCompany', 'GET')")
    public TransportCompany findTransportForCompanyById(Long id, Long warehouseCompanyId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        return findTransportCompanyById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyByTransportId(Long transportId)
            throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse company of transport with id {}", transportId);

        if (transportId == null) {
            throw new IllegalParametersException("Transport company id is null");
        }

        TransportCompany company = findTransportCompanyById(transportId);
        if (company == null) {
            throw new ResourceNotFoundException("Transport company with such id was not found");
        }

        return company.getWarehouseCompany();
    }

    @Override
    @Transactional
    //todo @PreAuthorize("hasPermission('WarehouseCustomerCompany', 'WRITE')")
    public TransportCompany saveTransportCompany(TransportCompanyDTO dto, WarehouseCompany warehouseCompany) throws DataAccessException {
        logger.info("Save transport dto: {}", dto);

        TransportCompany savedCompany;
        try {
            TransportCompany transportCompany = mapToCompany(dto);
            transportCompany.setWarehouseCompany(warehouseCompany);

            savedCompany = transportDAO.insert(transportCompany);
        } catch (GenericDAOException e) {
            logger.error("Error while saving transport dto: ", e);
            throw new DataAccessException(e);
        }

        return savedCompany;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'TransportCompany', 'UPDATE')")
    public TransportCompany updateTransportCompany(Long id, TransportCompanyDTO dto, Long warehouseCompanyId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Update transport dto: {}", dto);

        TransportCompany updatedCompany;
        try {
            dto.setId(id);

                TransportCompany company = mapToCompany(dto);
                WarehouseCompany companyOfTransportCompany = companyService.findWarehouseCompanyById(String.valueOf(warehouseCompanyId));
                company.setWarehouseCompany(companyOfTransportCompany);

            updatedCompany = transportDAO.update(company);
        } catch (GenericDAOException e) {
            logger.error("Error while updating customer dto: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'TransportCompany', 'DELETE')")
    public void deleteTransportCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Delete transport dto by id #{}", id);

        try {
            Optional<TransportCompany> optional = transportDAO.findById(id);
            if (optional.isPresent()) {
                TransportCompany company = optional.get();
                transportDAO.delete(company);
            } else {
                logger.error("Transport dto with id {} not found", id);
                throw new ResourceNotFoundException("Transport dto not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting transport dto: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#warehouseCompany.idWarehouseCompany, 'WarehouseCompany', 'GET')")
    public List<TransportCompany> searchSimilarToCompanyForWarehouseCompany(TransportCompanyDTO dto, WarehouseCompany warehouseCompany) {
        ElasticSearchTransportCompany searchTransportCompany = new ElasticSearchTransportCompany();
        TransportCompany company = mapToCompany(dto);
        company.setWarehouseCompany(warehouseCompany);

        List<SimilarityWrapper<TransportCompany>> companiesByRelevance = searchTransportCompany.search(company);
        return companiesByRelevance.stream().map(SimilarityWrapper::getOjbect).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean TransportCompanyExists(TransportCompany company) throws DataAccessException {
        logger.error("Determine if transport dto #{} exists", company.getId());

        try {
            return transportDAO.isExistsEntity(company.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if transport dto exists", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    public TransportCompany mapToCompany(TransportCompanyDTO dto) {
        TransportCompany company = new TransportCompany();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setTrusted(dto.getIsTrusted());
        company.setLogin(dto.getLogin());
        company.setPassword(dto.getPassword());

        return company;
    }

    @Override
    public TransportCompanyDTO mapToDto(TransportCompany company) {
        TransportCompanyDTO dto = new TransportCompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());

        return dto;
    }
}
