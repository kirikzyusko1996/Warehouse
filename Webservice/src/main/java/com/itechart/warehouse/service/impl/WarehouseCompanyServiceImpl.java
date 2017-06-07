package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.WarehouseCompanyDAO;
import com.itechart.warehouse.dao.WarehouseCompanyStatusDAO;
import com.itechart.warehouse.dao.WarehouseDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCompanyStatus;
import com.itechart.warehouse.mail.EmailSenderService;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Lenovo on 25.04.2017.
 */
@Service
public class WarehouseCompanyServiceImpl implements WarehouseCompanyService {
    private WarehouseCompanyDAO warehouseCompanyDAO;
    private WarehouseCompanyStatusDAO statusDAO;
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(WarehouseCompanyServiceImpl.class);
    private EmailSenderService emailSenderService;

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Lazy
    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Autowired
    public void setWarehouseCompanyDAO(WarehouseCompanyDAO warehouseCompanyDAO) {
        this.warehouseCompanyDAO = warehouseCompanyDAO;
    }

    @Autowired
    public void setStatusDAO(WarehouseCompanyStatusDAO dao) {
        this.statusDAO = dao;
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
    public List<WarehouseCompany> findWarehouseCompany(Long id_user) throws DataAccessException, IllegalParametersException {
        if(id_user == null) {
            throw new IllegalParametersException("Invalid id param");
        }
        logger.info("Find all warehouse companies");
        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCompany.class);
        List<WarehouseCompany> warehouseCompanies = null;
        criteria.add(Restrictions.eq("id", id_user));
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
    public Set<WarehouseCompany> findAllThatUsedForPeriod(Date startDate, Date dueDate)
            throws DataAccessException, IllegalParametersException {
        logger.info("Find all warehouse companies, that used system from {} to {}", startDate, dueDate);

        if (dueDate.after(today())) {
            logger.error("Due date cannot be in the future");
            throw new IllegalParametersException("Due date cannot be in the future");
        }

        if (startDate.after(dueDate)) {
            logger.error("Start date cannot be in the after due date");
            throw new IllegalParametersException("Start date cannot be in the after due date");
        }

        Set<WarehouseCompany> companies;
        try {
            List<WarehouseCompanyStatus> statuses = retrieveStatusesForPeriod(startDate, dueDate);
            companies = statuses.stream()
                    .map(WarehouseCompanyStatus::getWarehouseCompany)
                    .collect(Collectors.toSet());

        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse companies that used system for some period", e.getMessage());
            throw new DataAccessException(e);
        }

        return companies;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id_warehouse, 'WarehouseCompany', 'GET')")
    public WarehouseCompany getWarehouseCompanyById(Long id_warehouse) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        return findWarehouseCompanyById(id_warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find warehouse by id: {}", id);

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
    //@PreAuthorize("hasPermission(#warehouseCompany.idWarehouseCompany, 'WarehouseCompany', 'POST')")
    public User saveWarehouseCompany(WarehouseCompany warehouseCompany, String email) throws DataAccessException {
        logger.info("Saving WarehouseCompany: {}", warehouseCompany);
        WarehouseCompany updatedWarehouseCompany = null;
        User user = null;
        try {
            updatedWarehouseCompany = warehouseCompanyDAO.insert(warehouseCompany);
            user = userService.createSupervisor(updatedWarehouseCompany.getIdWarehouseCompany());
            user.setEmail(email);
            if(!emailSenderService.sendMessageAboutRegistration(user)) {
                return null;
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving WarehouseCompany: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        } catch (ResourceNotFoundException e) {
            logger.error("Error during creating supervisor: {}", e.getMessage());
        } catch (IllegalParametersException e) {
            logger.error("Error during creating supervisor: {}", e.getMessage());
        }
        return user;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'WarehouseCompany', 'GET')")
    public WarehouseCompany updateWarehouseCompany(Long id, WarehouseCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update company: {}", company);

        WarehouseCompany updatedCompany;
        try {
            if (warehouseCompanyDAO.isExistsEntity(id)) {
                company.setIdWarehouseCompany(id);
                updatedCompany = warehouseCompanyDAO.update(company);
            } else {
                logger.error("Company with id {} not found", id);
                throw new ResourceNotFoundException("Company not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating company: ", e);
            throw new DataAccessException(e);
        }

        return updatedCompany;
    }

    /**
     * Because this method don't delete really in the database
     * and merely change status, this method can call twice:
     * when you "delete" entity and "restore" entity,
     * so this method just change status to opposite
     * */
    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'WarehouseCompany', 'GET')")
    public void deleteWarehouseCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting act with id: {}", id);

        try {
            Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(id);
            if (result != null) {
                result.get().setStatus(!result.get().getStatus());//so can recovery it, merely change status to opposite
                warehouseCompanyDAO.update(result.get());
            }
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

    private List<WarehouseCompanyStatus> retrieveStatusesForPeriod(Date startDate, Date dueDate) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCompanyStatus.class);
        Conjunction and = Restrictions.conjunction();
        and.add(Restrictions.eq("status", Boolean.TRUE));
        and.add(Restrictions.le("startDate", dueDate));
        and.add(Restrictions.ge("dueDate", startDate));
        criteria.add(and);

        return statusDAO.findAll(criteria, -1, -1);
    }

    private Date today() {
        return new Date(new java.util.Date().getTime());
    }
}
