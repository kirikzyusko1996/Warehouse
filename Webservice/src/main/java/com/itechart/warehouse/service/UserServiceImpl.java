package com.itechart.warehouse.service;

import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.exceptions.GenericDAOException;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Implementation of user service.
 */
@Service
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("")
    public List<User> findAllUsers() throws DataAccessException {
        logger.info("Find all users");
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        List<User> users = null;
        try {
            users = userDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) throws DataAccessException {
        logger.info("Find user by id: {}", id);
        if (id == null) return null;
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("id", id));
        User user = null;
        try {
            Optional<User> result = userDAO.findById(id);
            user = result.get();
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByLogin(String login) throws DataAccessException {
        logger.info("Find user by login name: {}", login);
        if (login == null) return null;
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", login));
        List<User> users = null;
        try {
            return (users = userDAO.findAll(criteria, -1, -1)).isEmpty() ? null : users.get(0);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersForCompany(Long companyId) throws DataAccessException {
        logger.info("Find user by company id: {}", companyId);
        if (companyId == null) return null;
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("company_id", companyId));
        List<User> users = null;
        try {
            users = userDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return users;
    }

    @Override
    @Transactional
    public User saveUser(User user) throws DataAccessException {
        logger.info("Saving user: {}", user);
        User updatedUser = null;
        try {
            if (userDAO.isExistsEntity(user.getIdUser())) {
                updatedUser = userDAO.update(user);
            } else {
                updatedUser = userDAO.insert(user);
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(User user) throws DataAccessException {
        logger.info("Deleting user: {}", user);
        try {
            userDAO.delete(user);
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(User user) throws DataAccessException {
        try {
            return userDAO.isExistsEntity(user.getIdUser());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if user with id: {} exists", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
