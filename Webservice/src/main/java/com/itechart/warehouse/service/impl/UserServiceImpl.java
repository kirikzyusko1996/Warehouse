package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.dao.RoleDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.RoleDTO;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of user service.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String ERROR_USER_ID_IS_NULL = "User id is null";
    private static final String ERROR_USER_DTO_IS_NULL = "User DTO is null";
    private static final String ERROR_ID_IS_NULL = "Id is null";
    private static final String ERROR_UPDATABLE_USER_IS_NULL = "Updatable user is null";
    private static final String ERROR_ROLE_IS_NULL = "Role is null";

    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private WarehouseService warehouseService;
    private WarehouseCompanyService warehouseCompanyService;
    //todo uncomment
//    private BCryptPasswordEncoder encoder;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    //todo uncomment
//    @Autowired
//    public void setEncoder(BCryptPasswordEncoder encoder) {
//        this.encoder = encoder;
//    }

    @Autowired
    @Lazy
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Autowired
    @Lazy
    public void setWarehouseCompanyService(WarehouseCompanyService warehouseCompanyService) {
        this.warehouseCompanyService = warehouseCompanyService;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Autowired
    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }


    @Override
    @Transactional(readOnly = true)
    //todo secured version
//    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'User', 'GET')")
    public UserDTO findUserDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user DTO, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            User user = userDAO.findUserById(id);
            if (user != null) {
                return mapUserToDTO(user);
            } else {
                throw new ResourceNotFoundException("User with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            User user = userDAO.findUserById(id);
            if (user != null) {
                return user;
            } else {
                throw new ResourceNotFoundException("User with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByLogin(String login) throws DataAccessException, IllegalParametersException {
        logger.info("Find user, login: {}", login);
        if (login == null) {
            throw new IllegalParametersException("Login is null");
        }

        try {
            return userDAO.findUserByLogin(login);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#companyId, 'WarehouseCompany', 'GET')")
    public List<UserDTO> findUsersForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find users, first result: {}, max results: {}, company id: {}", firstResult, maxResults, companyId);
        if (companyId == null) {
            throw new IllegalParametersException("Company id is null");
        }

        try {
            List<User> users = userDAO.findUsersByWarehouseCompanyId(companyId, firstResult, maxResults);
            return mapUserListToDTOList(users);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUsersCount(Long companyId) throws DataAccessException, IllegalParametersException {
        logger.info("Get users count, company id: {}", companyId);
        if (companyId == null) {
            throw new IllegalParametersException("Company id is null");
        }

        try {
            return userDAO.getUsersCount(companyId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUserByBirthday(DateTime date) throws IllegalParametersException, DataAccessException {
        logger.info("Find users with birthday, date: {}", date);
        if (date == null) {
            throw new IllegalParametersException("Date is null");
        }

        try {
            return userDAO.findUsersByBirthDay(date);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private boolean isLoginOccupied(String loginName) throws IllegalParametersException, DataAccessException {
        logger.info("Check if login is occupied, login {}", loginName);
        if (loginName == null) {
            throw new IllegalParametersException("Login is null");
        }

        User user = findUserByLogin(loginName);
        return user != null;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#companyId, 'WarehouseCompany', 'CREATE')")
    public User saveUser(Long companyId, UserDTO userDTO) throws DataAccessException, ResourceNotFoundException, IllegalParametersException {
        logger.info("Save user, DTO: {}", userDTO);
        if (userDTO == null) {
            throw new IllegalParametersException(ERROR_USER_DTO_IS_NULL);
        }

        try {
            validateRequiredFields(userDTO);
            User user = createUser(userDTO);
            if (userDTO.getWarehouse() != null) {
                setWarehouseField(userDTO.getWarehouse().getIdWarehouse(), user);
            }
            setWarehouseCompanyField(companyId, user);
            setRoles(user, userDTO.getRoles());

            //todo uncomment
//            if (StringUtils.isNotBlank(user.getPassword())) {
//                user.setPassword(encoder.encode(user.getPassword()));
//            }

            return userDAO.insert(user);

        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private boolean hasRole(User user, UserRoleEnum role) {
        Assert.notNull(user, "User is null");
        Assert.notNull(role, ERROR_ROLE_IS_NULL);

        return user.hasRole(role.toString());
    }

    private User getAuthenticatedUser() throws ResourceNotFoundException, DataAccessException, IllegalParametersException {
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            Long userId = userDetails.getUserId();
            return findUserById(userId);
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }
    }

    private void setRole(User updatableUser, UserRoleEnum role) throws IllegalParametersException, DataAccessException {
        Assert.notNull(updatableUser, ERROR_UPDATABLE_USER_IS_NULL);
        Assert.notNull(role, ERROR_ROLE_IS_NULL);

        Role r = findRoleByName(role.toString());
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(r));
        updatableUser.setRoles(roles);
    }

    private void setRoles(User updatableUser, List<RoleDTO> roleDTOList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        Assert.notNull(updatableUser, ERROR_UPDATABLE_USER_IS_NULL);
        Assert.notNull(roleDTOList, "Role DTO list is null");

        if (CollectionUtils.isNotEmpty(roleDTOList)) {
            User authenticatedUser = getAuthenticatedUser();
            List<Role> roles = new ArrayList<>();
            for (RoleDTO role : roleDTOList) {
                if (role.getRole().equals(UserRoleEnum.ROLE_ADMIN.toString())) {
                    if (hasRole(authenticatedUser, UserRoleEnum.ROLE_ADMIN)) {
                        roles.add(findRoleByName(role.getRole()));
                    }
                } else {
                    roles.add(findRoleByName(role.getRole()));
                }
                updatableUser.setRoles(roles);
            }
        } else {
            throw new IllegalParametersException("At least one role has to be selected");
        }

    }

    private void setWarehouseField(Long warehouseId, User updatableUser) throws DataAccessException, IllegalParametersException {
        Assert.notNull(warehouseId, "Warehouse id is null");
        Assert.notNull(updatableUser, ERROR_UPDATABLE_USER_IS_NULL);

        if (warehouseId != null) {
            Warehouse warehouse = warehouseService.findWarehouseById(warehouseId);
            updatableUser.setWarehouse(warehouse);
        }
    }

    private void setWarehouseCompanyField(Long warehouseCompanyId, User updatableUser) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        Assert.notNull(warehouseCompanyId, "Warehouse company id is null");
        Assert.notNull(updatableUser, ERROR_UPDATABLE_USER_IS_NULL);

        WarehouseCompany warehouseCompany = warehouseCompanyService.findWarehouseCompanyById(warehouseCompanyId);
        updatableUser.setWarehouseCompany(warehouseCompany);

    }

    private void validateRequiredFields(UserDTO userDTO) throws IllegalParametersException {
        Assert.notNull(userDTO, ERROR_USER_DTO_IS_NULL);

        if (StringUtils.isBlank(userDTO.getPassword())) {
            throw new IllegalParametersException("Field password can not be empty");
        }
        if (StringUtils.isBlank(userDTO.getLogin())) {
            throw new IllegalParametersException("Field login can not be empty");
        }
        if (StringUtils.isBlank(userDTO.getLastName())) {
            throw new IllegalParametersException("Field last name can not be empty");
        }
    }


    private User createUser(UserDTO userDTO) {
        Assert.notNull(userDTO, ERROR_USER_DTO_IS_NULL);

        User user = new User();

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPatronymic(userDTO.getPatronymic());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setCity(userDTO.getCity());
        user.setStreet(userDTO.getStreet());
        user.setHouse(userDTO.getHouse());
        user.setApartment(userDTO.getApartment());
        user.setEmail(userDTO.getEmail());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());

        return user;
    }


    @Override
    @Transactional
    public User createSupervisor(Long warehouseId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create supervisor, warehouse id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException("Warehouse id is null");
        }

        try {
            User user = new User();
            setWarehouseField(warehouseId, user);

            Warehouse warehouse = warehouseService.findWarehouseById(warehouseId);
            WarehouseCompany warehouseCompany = warehouse.getWarehouseCompany();
            setWarehouseCompanyField(warehouseCompany.getIdWarehouseCompany(), user);

            user.setLastName(warehouseCompany.getName());
            user.setLogin(generateRandomCredentials(5));
            user.setPassword(generateRandomCredentials(5));
            //todo uncomment
//                if (StringUtils.isNotBlank(user.getPassword())) {
//                    user.setPassword(encoder.encode(RandomStringUtils.randomAlphanumeric(5)));
//                }

            setRole(user, UserRoleEnum.ROLE_ADMIN);
            return userDAO.insert(user);

        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private String generateRandomCredentials(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    private Role findRoleByName(String roleName) throws IllegalParametersException, DataAccessException {
        logger.info("Find role, name: {}", roleName);
        if (roleName == null) {
            throw new IllegalParametersException("Role name is null");
        }

        try {
            return roleDAO.findRoleByName(roleName);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
//    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'UPDATE')")
    public User updateUser(Long id, UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update user, id: {}, DTO: {}", id, userDTO);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }
        if (userDTO == null) {
            throw new IllegalParametersException(ERROR_USER_DTO_IS_NULL);
        }

        try {
            User user = userDAO.findUserById(id);
            if (user != null) {
                updateUserFromDTO(user, userDTO);

                //todo uncomment
//                if (StringUtils.isNotBlank(userDTO.getPassword())) {
//                    user.setPassword(encoder.encode(userDTO.getPassword()));
//                }

                return user;
            } else {
                throw new ResourceNotFoundException("User with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private User updateUserFromDTO(User updatableUser, UserDTO userDTO) throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        Assert.notNull(updatableUser, ERROR_UPDATABLE_USER_IS_NULL);
        Assert.notNull(userDTO, ERROR_USER_DTO_IS_NULL);

        if (StringUtils.isNotBlank(userDTO.getLastName())) {
            updatableUser.setLastName(userDTO.getLastName());
        } else {
            throw new IllegalParametersException("Field last name can not be empty");
        }

        if (StringUtils.isNotBlank(userDTO.getLogin()) && !updatableUser.getLogin().equals(userDTO.getLogin())) {
            if (!isLoginOccupied(userDTO.getLogin())) {
                updatableUser.setLogin(userDTO.getLogin());
            } else {
                throw new IllegalParametersException("Login name is occupied");
            }
        }

        if (userDTO.getWarehouse() != null) {
            setWarehouseField(userDTO.getWarehouse().getIdWarehouse(), updatableUser);
        }

        updatableUser.setFirstName(userDTO.getFirstName());

        updatableUser.setPatronymic(userDTO.getPatronymic());
        updatableUser.setDateOfBirth(userDTO.getDateOfBirth());
        updatableUser.setCity((userDTO.getCity()));
        updatableUser.setStreet(userDTO.getStreet());
        updatableUser.setHouse(userDTO.getHouse());
        updatableUser.setApartment(userDTO.getApartment());
        updatableUser.setEmail(userDTO.getEmail());

        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            updatableUser.setPassword(userDTO.getPassword());
        }
        setRoles(updatableUser, userDTO.getRoles());

        return updatableUser;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'DELETE')")
    public void deleteUser(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete user, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        User user = findUserById(id);
        user.setDeleted(new Date(DateTime.now().toDate().getTime()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Check if user exists, id {} exists", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            return userDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, UserRoleEnum role) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Check if user has role, id {}, role {}", userId, role);
        if (userId == null) {
            throw new IllegalParametersException(ERROR_USER_ID_IS_NULL);
        }
        if (role == null) {
            throw new IllegalParametersException(ERROR_ROLE_IS_NULL);
        }

        try {
            Role r = roleDAO.findRoleByName(role.toString());
            User user = findUserById(userId);
            return user.getRoles().contains(r);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRoles() throws DataAccessException {
        logger.info("Get roles");

        try {
            List<Role> roleList = roleDAO.getRoles();
            return mapRoleListToDTOList(roleList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private UserDTO mapUserToDTO(User user) {
        Assert.notNull(user, "User is null");

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPatronymic(user.getPatronymic());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setCity(user.getCity());
        userDTO.setStreet(user.getStreet());
        userDTO.setHouse(user.getHouse());
        userDTO.setApartment(user.getApartment());
        userDTO.setEmail(user.getEmail());
        userDTO.setLogin(user.getLogin());
        userDTO.setPassword(user.getPassword());
        userDTO.setWarehouse(user.getWarehouse());
        userDTO.setWarehouseCompany(user.getWarehouseCompany());

        userDTO.setRoles(mapRoleListToDTOList(user.getRoles()));

        return userDTO;
    }


    private List<UserDTO> mapUserListToDTOList(List<User> userList) {
        Assert.notNull(userList, "User list is null");

        List<UserDTO> dtoList = new ArrayList<>();
        for (User user : userList) {
            dtoList.add(mapUserToDTO(user));
        }
        return dtoList;
    }

    private RoleDTO mapRoleToDTO(Role role) {
        Assert.notNull(role, ERROR_ROLE_IS_NULL);

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setIdRole(role.getIdRole());
        roleDTO.setRole(role.getRole());

        return roleDTO;
    }

    private List<RoleDTO> mapRoleListToDTOList(List<Role> roleList) {
        Assert.notNull(roleList, "Role list is null");

        List<RoleDTO> dtoList = new ArrayList<>();
        for (Role role : roleList) {
            dtoList.add(mapRoleToDTO(role));
        }
        return dtoList;
    }


}
