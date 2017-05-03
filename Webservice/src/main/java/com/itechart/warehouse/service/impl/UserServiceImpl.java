package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.dao.RoleDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.WarehouseCompanyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Implementation of user service.
 */
@Service
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private WarehouseCompanyDAO warehouseCompanyDAO;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Autowired
    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Autowired
    public void setWarehouseCompanyDAO(WarehouseCompanyDAO warehouseCompanyDAO) {
        this.warehouseCompanyDAO = warehouseCompanyDAO;
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("")
    public List<User> findAllUsers(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find {} users starting from index {}", maxResults, firstResult);
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        try {
            return userDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user by id: {}", id);
        if (id == null)
            throw new IllegalParametersException("Id is null");
        try {
            User user = userDAO.findUserById(id);
            if (user != null)
                return user;
            else throw new ResourceNotFoundException("User with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByLogin(String login) throws DataAccessException, IllegalParametersException {
        logger.info("Find user by login name: {}", login);
        if (login == null) throw new IllegalParametersException("Login is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", login));
        try {
            return userDAO.findAll(criteria, -1, -1).get(0);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} users starting from index {} by company id: {}", maxResults, firstResult, companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            return userDAO.findUsersByWarehouseCompanyId(companyId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public User createUser(Long companyId, UserDTO userDTO) throws DataAccessException, ResourceNotFoundException, IllegalParametersException {
        logger.info("Saving user using DTO: {}", userDTO);
        if (userDTO == null) throw new IllegalParametersException("User DTO is null");
        try {
            User user = userDTO.buildUserEntity();
            if (StringUtils.isBlank(user.getPassword())) {
                throw new IllegalParametersException("Field password can not be empty");
            }
            if (StringUtils.isBlank(user.getLogin())) {
                throw new IllegalParametersException("Field login can not be empty");
            }
            if (StringUtils.isBlank(user.getLastName())) {
                throw new IllegalParametersException("Field last name can not be empty");
            }
            WarehouseCompany warehouseCompany = findWarehouseCompanyById(companyId);
            user.setWarehouseCompany(warehouseCompany);
            List<String> roleNames = userDTO.getRoles();
            if (roleNames != null) {
                for (String roleName : roleNames) {
                    try {
                        List roles = new ArrayList();
                        roles.add(findRoleByName(roleName));
                        user.setRoles(roles);
                    } catch (IllegalParametersException e) {
                        logger.error("Role was not found: {}", e.getMessage());
                    }
                }
            } else throw new IllegalParametersException("At least one role has to be selected");
            return userDAO.insert(user);
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    private WarehouseCompany findWarehouseCompanyById(Long companyId) throws IllegalParametersException, GenericDAOException, ResourceNotFoundException {
        logger.info("Searching for company with id: {}", companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(companyId);
        if (result.isPresent()) {
            return result.get();
        } else throw new ResourceNotFoundException("Warehouse company with such id was not found");

    }

    @Override
    @Transactional
    public User createSupervisor(Long companyId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create supervisor for company with id: {}", companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(companyId);
            WarehouseCompany company = null;
            if (result.isPresent()) {
                company = result.get();
                User user = new User();
                user.setLastName(company.getName());
                user.setWarehouseCompany(company);
                user.setLogin(RandomStringUtils.randomAlphanumeric(5));
                user.setPassword(RandomStringUtils.randomAlphanumeric(5));
                Role role = findRoleByName(UserRoleEnum.ROLE_ADMIN.toString());
                List<Role> roles = new ArrayList<>();
                if (role != null)
                    roles.add(role);
                user.setRoles(roles);
                return userDAO.insert(user);
            } else throw new ResourceNotFoundException("Company with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    //    @Transactional(readOnly = true)
    private Role findRoleByName(String roleName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for role with name: {}", roleName);
        if (roleName == null) throw new IllegalParametersException("Role name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("role", roleName));
        List<Role> fetchedRoles = roleDAO.findAll(criteria, -1, 1);
        if (!fetchedRoles.isEmpty())
            return fetchedRoles.get(0);
        else throw new IllegalParametersException("Invalid role name: " + roleName);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating user with id: {} from DTO: {}", id, userDTO);
        if (id == null || userDTO == null) throw new IllegalParametersException("User DTO or id is null");
        try {
            User user = userDAO.findUserById(id);
            if (user != null) {
                user.setFirstName(userDTO.getFirstName());
                if (StringUtils.isNotBlank(userDTO.getLastName()))
                    user.setLastName(userDTO.getLastName());
                else throw new IllegalParametersException("Field last name can not be empty");
                user.setPatronymic(userDTO.getPatronymic());
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setCity((userDTO.getCity()));
                user.setStreet(userDTO.getStreet());
                user.setHouse(userDTO.getHouse());
                user.setApartment(userDTO.getApartment());
                user.setEmail(userDTO.getEmail());
                if (StringUtils.isNotBlank(userDTO.getLogin()))
                    user.setLogin(userDTO.getLogin());
                else throw new IllegalParametersException("Filed login can not be empty");
                if (StringUtils.isNotBlank(userDTO.getPassword()))
                    user.setPassword(userDTO.getPassword());
                else throw new IllegalParametersException("Field password can not be empty");
                List<String> roleNames = userDTO.getRoles();
                if (roleNames != null)
                    for (String roleName : roleNames) {
                        user.addRole(findRoleByName(roleName));
                    }
                else {
                    throw new IllegalParametersException("At least one role has to be selected");
                }
                return userDAO.update(user);
            } else throw new ResourceNotFoundException("User with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during updating user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting user with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<User> result = userDAO.findById(id);
            if (result != null)
                userDAO.delete(result.get());
            else throw new ResourceNotFoundException("User with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Checking if user with id {} exists", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            return userDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            logger.error("Error while determine if user exists: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
