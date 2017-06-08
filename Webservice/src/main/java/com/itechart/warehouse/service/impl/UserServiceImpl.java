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
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import java.util.List;
import java.util.Optional;


/**
 * Implementation of user service.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String ERROR_USER_ID_IS_NULL = "User id is null";
    private static final String ERROR_USER_DTO_IS_NULL = "User DTO is null";
    private static final String ERROR_ID_IS_NULL = "Id is null";

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
    public List<User> findAllUsers(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find users, first result {}, max results: {}", firstResult, maxResults);

        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.addOrder(Order.asc("id"));
        try {
            return userDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
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
    public User findUserByLogin(String login) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user, login: {}", login);
        if (login == null) {
            throw new IllegalParametersException("Login is null");
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", login));
        criteria.add(Restrictions.isNull("deleted"));
        try {
            List<User> users = userDAO.findAll(criteria, -1, -1);
            if (CollectionUtils.isNotEmpty(users)) {
                return users.get(0);
            } else {
                return null;
            }
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
            return mapUserListToDTOList(userDAO.findUsersByWarehouseCompanyId(companyId, firstResult, maxResults));
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
    public Warehouse findWarehouseOwnedBy(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find warehouse for user, user id: {}", userId);
        if (userId == null) {
            throw new IllegalParametersException(ERROR_USER_ID_IS_NULL);
        }
        UserDTO user = findUserDTOById(userId);
        return user.getWarehouse();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwnedBy(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find warehouse company for user, user id: {}", userId);
        if (userId == null) {
            throw new IllegalParametersException(ERROR_USER_ID_IS_NULL);
        }
        UserDTO user = findUserDTOById(userId);
        return user.getWarehouseCompany();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find users, first result: {}, max results: {}, warehouse id: {}", firstResult, maxResults, warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException("Warehouse id is null");
        }
        try {
            return userDAO.findUsersByWarehouseId(warehouseId, firstResult, maxResults);
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
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", loginName));
        criteria.add(Restrictions.isNull("deleted"));
        try {
            List<User> users = userDAO.findAll(criteria, -1, -1);
            return (CollectionUtils.isNotEmpty(users));
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
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
            User user = createUser(userDTO);
            if (StringUtils.isBlank(user.getPassword())) {
                throw new IllegalParametersException("Field password can not be empty");
            }
            if (StringUtils.isBlank(user.getLogin())) {
                throw new IllegalParametersException("Field login can not be empty");
            }
            if (StringUtils.isBlank(user.getLastName())) {
                throw new IllegalParametersException("Field last name can not be empty");
            }
            if (userDTO.getWarehouse() != null && userDTO.getWarehouse().getIdWarehouse() != null) {
                Warehouse warehouse = warehouseService.findWarehouseById(userDTO.getWarehouse().getIdWarehouse());
                user.setWarehouse(warehouse);
            }
            //todo uncomment
//            if (StringUtils.isNotBlank(user.getPassword())) {
//                user.setPassword(encoder.encode(user.getPassword()));
//            }
            WarehouseCompany warehouseCompany = warehouseCompanyService.findWarehouseCompanyById(companyId);
            user.setWarehouseCompany(warehouseCompany);
            List<Role> roles = new ArrayList<>();
            if (userDTO.getRoles() != null) {
                for (RoleDTO role : userDTO.getRoles()) {
                    if (role.getRole().equals(UserRoleEnum.ROLE_ADMIN.toString())) {
                        if (UserDetailsProvider.getUserDetails().getUser() != null && UserDetailsProvider.getUserDetails().getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())) {
                            roles.add(findRoleByName(role.getRole()));
                        }
                    } else {
                        roles.add(findRoleByName(role.getRole()));
                    }
                    user.setRoles(roles);
                }
            } else throw new IllegalParametersException("At least one role has to be selected");
            return userDAO.insert(user);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private User createUser(UserDTO userDTO) {
        Assert.notNull(userDTO, ERROR_USER_DTO_IS_NULL);

        User user = new User();

        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPatronymic(user.getPatronymic());
        user.setDateOfBirth(user.getDateOfBirth());
        user.setCity(user.getCity());
        user.setStreet(user.getStreet());
        user.setHouse(user.getHouse());
        user.setApartment(user.getApartment());
        user.setEmail(user.getEmail());
        user.setLogin(user.getLogin());
        user.setPassword(user.getPassword());

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
            Warehouse warehouse = warehouseService.findWarehouseById(warehouseId);
            if (warehouse != null) {
                User user = new User();
                user.setWarehouse(warehouse);
                user.setWarehouseCompany(warehouse.getWarehouseCompany());
                user.setLastName(warehouse.getWarehouseCompany().getName());
                user.setLogin(RandomStringUtils.randomAlphanumeric(5));
                user.setPassword(RandomStringUtils.randomAlphanumeric(5));
                //todo uncomment
//                if (StringUtils.isNotBlank(user.getPassword())) {
//                    user.setPassword(encoder.encode(RandomStringUtils.randomAlphanumeric(5)));
//                }

                Role role = findRoleByName(UserRoleEnum.ROLE_ADMIN.toString());
                List<Role> roles = new ArrayList<>();
                if (role != null) {
                    roles.add(findRoleByName(role.getRole()));
                }
                user.setRoles(roles);
                return userDAO.insert(user);
            } else throw new ResourceNotFoundException("Company with id " + warehouseId + "was not found");
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private Role findRoleByName(String roleName) throws GenericDAOException, IllegalParametersException {
        logger.info("Find role, name: {}", roleName);
        if (roleName == null) {
            throw new IllegalParametersException("Role name is null");
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("role", roleName));
        List<Role> fetchedRoles = roleDAO.findAll(criteria, -1, 1);
        if (!fetchedRoles.isEmpty()) {
            return fetchedRoles.get(0);
        } else {
            throw new IllegalParametersException("Invalid role name: " + roleName);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'UPDATE')")
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
                if (userDTO.getWarehouse() != null && userDTO.getWarehouse().getIdWarehouse() != null) {
                    Warehouse warehouse = warehouseService.findWarehouseById(userDTO.getWarehouse().getIdWarehouse());
                    user.setWarehouse(warehouse);
                }
                user.setFirstName(userDTO.getFirstName());
                if (StringUtils.isNotBlank(userDTO.getLastName())) {
                    user.setLastName(userDTO.getLastName());
                } else {
                    throw new IllegalParametersException("Field last name can not be empty");
                }
                user.setPatronymic(userDTO.getPatronymic());
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setCity((userDTO.getCity()));
                user.setStreet(userDTO.getStreet());
                user.setHouse(userDTO.getHouse());
                user.setApartment(userDTO.getApartment());
                user.setEmail(userDTO.getEmail());
                if (StringUtils.isNotBlank(userDTO.getLogin()) && !user.getLogin().equals(userDTO.getLogin())) {
                    if (!isLoginOccupied(userDTO.getLogin())) {
                        user.setLogin(userDTO.getLogin());
                    } else {
                        throw new IllegalParametersException("Login name is occupied");
                    }

                }
                if (StringUtils.isNotBlank(userDTO.getPassword())) {
                    user.setPassword(userDTO.getPassword());
                }
                //todo uncomment
//                if (StringUtils.isNotBlank(userDTO.getPassword())) {
//                    user.setPassword(encoder.encode(userDTO.getPassword()));
//                }

                List<RoleDTO> roles = userDTO.getRoles();
                List<Role> newRoles = new ArrayList<>();

                if (roles != null) {
                    for (RoleDTO role : roles) {
                        if (role.getRole().equals(UserRoleEnum.ROLE_ADMIN.toString())) {
                            if (UserDetailsProvider.getUserDetails().getUser() != null && UserDetailsProvider.getUserDetails().getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())) {
                                newRoles.add(findRoleByName(role.getRole()));
                            }
                        } else {
                            newRoles.add(findRoleByName(role.getRole()));
                        }
                    }
                    user.setRoles(newRoles);
                } else {
                    throw new IllegalParametersException("At least one role has to be selected");
                }
                return userDAO.update(user);
            } else throw new ResourceNotFoundException("User with id " + id + " was not found");
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'DELETE')")
    public void deleteUser(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete user, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            Optional<User> result = userDAO.findById(id);
            if (result.isPresent()) {
                User user = result.get();
                user.setDeleted(new Date(DateTime.now().toDate().getTime()));
            } else {
                throw new ResourceNotFoundException("User with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
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
            throw new IllegalParametersException("Role is null");
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("role", role.toString()));
        try {
            List<Role> fetchedRoles = roleDAO.findAll(criteria, -1, -1);
            if (fetchedRoles.isEmpty()) {
                throw new ResourceNotFoundException("Role " + role.toString() + " was not found");
            }
            Role foundRole = fetchedRoles.get(0);
            User user = findUserById(userId);
            if (user == null) {
                throw new ResourceNotFoundException("User with id " + userId + " was not found");
            }
            return user.getRoles().contains(foundRole);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRoles() throws DataAccessException {
        logger.info("Get roles");

        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        try {
            List<Role> roleList = roleDAO.findAll(criteria, -1, -1);
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
        Assert.notNull(role, "Role is null");

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
