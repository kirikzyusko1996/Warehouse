package com.itechart.warehouse.service;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

/**
 * Service for managing users.
 * Provides basic operation with user such as searching, creation, updating and deleting.
 */
public interface UserService {
    List<User> findAllUsers() throws DataAccessException;

    User findUserById(Long id) throws DataAccessException;

    User findUserByLogin(String login) throws DataAccessException;

    List<User> findUsersForCompany(Long companyId) throws DataAccessException;

    User saveUser(User user) throws DataAccessException;

    void deleteUser(User user) throws DataAccessException;

    boolean isUserExists(User user) throws DataAccessException;
}
