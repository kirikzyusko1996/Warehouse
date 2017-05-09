package com.itechart.warehouse.service.services;

import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Service for managing users.
 * Provides basic operations with user such as searching, creation, updating and deleting.
 */
public interface UserService {
    List<User> findAllUsers(int firstResult, int maxResults) throws DataAccessException;

    User findUserById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    User findUserByLogin(String login) throws DataAccessException, IllegalParametersException;

    List<User> findUsersForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    long getUsersCount(Long companyId) throws DataAccessException, IllegalParametersException;

    Warehouse findWarehouseOwner(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    WarehouseCompany findWarehouseCompanyOwner(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<User> findUsersForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<User> findUserByBirthday(DateTime date) throws IllegalParametersException, DataAccessException;

    User createUser(Long companyId, UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    User createSupervisor(Long warehouseId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    User updateUser(Long id, UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteUser(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isUserExists(Long id) throws DataAccessException, IllegalParametersException;

    boolean hasRole(Long userId, UserRoleEnum role) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
}
