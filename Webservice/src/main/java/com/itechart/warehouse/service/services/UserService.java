package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;

import java.util.List;

/**
 * Service for managing users.
 * Provides basic operations with user such as searching, creation, updating and deleting.
 */
public interface UserService {
    List<User> findAllUsers(int firstResult, int maxResults) throws DataAccessException;

    User findUserById(Long id) throws DataAccessException, IllegalParametersException;

    User findUserByLogin(String login) throws DataAccessException, IllegalParametersException;

    List<User> findUsersForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    User createUser(UserDTO userDTO) throws DataAccessException, IllegalParametersException;

    User createSupervisor(Long companyId) throws DataAccessException, IllegalParametersException;

    User updateUser(Long id, UserDTO userDTO) throws DataAccessException, IllegalParametersException;

    void deleteUser(Long id) throws DataAccessException, IllegalParametersException;

    boolean isUserExists(Long id) throws DataAccessException, IllegalParametersException;
}
