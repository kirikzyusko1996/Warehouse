package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.TransportCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.TransportCompanyDTO;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.TransportCompanyService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
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
    public TransportCompany findTransportCompanyById(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Find transport dto by id #{}", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        TransportCompany company = null;
        try {
            Long companyId = Long.valueOf(id);
            if (transportDAO.isExistsEntity(companyId)) {
                Optional<TransportCompany> optional = transportDAO.findById(companyId);
                if (optional.isPresent()){
                    company = optional.get();
                }
            } else {
                logger.error("Transport dto with id {} not found", companyId);
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
    @Transactional
    public TransportCompany saveTransportCompany(TransportCompanyDTO dto, WarehouseCompany warehouseCompany) throws DataAccessException {
        logger.info("Save transport dto: {}", dto);

        TransportCompany savedCompany;
        try {
            TransportCompany transportCompany = mapToEntity(dto);
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
    public TransportCompany updateTransportCompany(String id, TransportCompanyDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Update transport dto: {}", dto);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        TransportCompany updatedCompany;
        try {
            Long companyId = Long.valueOf(id);
            if (transportDAO.isExistsEntity(companyId)) {
                dto.setId(companyId);

                TransportCompany company = mapToEntity(dto);
                WarehouseCompany companyOfTransportCompany = companyService.findWarehouseCompanyById(dto.getWarehouseCompanyId());
                company.setWarehouseCompany(companyOfTransportCompany);

                updatedCompany = transportDAO.update(company);
            } else {
                logger.error("Transport dto with id {} not found", companyId);
                throw new ResourceNotFoundException("transport dto not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating transport dto: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    @Override
    @Transactional
    public void deleteTransportCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException{
        logger.info("Delete transport dto by id #{}", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        try {
            Long companyId = Long.valueOf(id);
            Optional<TransportCompany> optional = transportDAO.findById(companyId);
            if (optional.isPresent()) {
                TransportCompany company = optional.get();
                transportDAO.delete(company);
            } else {
                logger.error("Transport dto with id {} not found", companyId);
                throw new ResourceNotFoundException("Transport dto not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting transport dto: ", e);
            throw new DataAccessException(e);
        }
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

    private TransportCompany mapToEntity(TransportCompanyDTO dto) {
        TransportCompany company = new TransportCompany();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setTrusted(dto.getIsTrusted());
        company.setLogin(dto.getLogin());
        company.setPassword(dto.getPassword());

        return company;
    }
}
