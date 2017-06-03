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
        logger.info("Find {} users starting from index {}", maxResults, firstResult);
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.addOrder(Order.asc("id"));
        try {
            return userDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    //todo secured version
//    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'User', 'GET')")
    public UserDTO findUserDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user DTO, id: {}", id);
        if (id == null)
            throw new IllegalParametersException("Id is null");
        try {
            UserDTO user = mapUserToDTO(userDAO.findUserById(id));
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
    public User findUserById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user, id: {}", id);
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
    public User findUserByLogin(String login) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find user by login name: {}", login);
        if (login == null) throw new IllegalParametersException("Login is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", login));
        criteria.add(Restrictions.isNull("deleted"));
        try {
            List<User> users = userDAO.findAll(criteria, -1, -1);
            if (CollectionUtils.isNotEmpty(users))
                return users.get(0);
            else return null;
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#companyId, 'WarehouseCompany', 'GET')")
    public List<UserDTO> findUsersForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} users starting from index {} by company id: {}", maxResults, firstResult, companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            return mapUserListToDTO(userDAO.findUsersByWarehouseCompanyId(companyId, firstResult, maxResults));
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUsersCount(Long companyId) throws DataAccessException, IllegalParametersException {
        logger.info("Get users count for company with id: {}", companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            return userDAO.getUsersCount(companyId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseOwnedBy(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find warehouse for user with id: {}", userId);
        if (userId == null) throw new IllegalParametersException("User id is null");
        UserDTO user = findUserDTOById(userId);
        return user.getWarehouse();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwnedBy(Long userId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find warehouse company for user with id: {}", userId);
        if (userId == null) throw new IllegalParametersException("User id is null");
        UserDTO user = findUserDTOById(userId);
        return user.getWarehouseCompany();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} users starting from index {} by warehouse id: {}", maxResults, firstResult, warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return userDAO.findUsersByWarehouseId(warehouseId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUserByBirthday(DateTime date) throws IllegalParametersException, DataAccessException {
        logger.info("Find users with birthday: {}", date);
        if (date == null) throw new IllegalParametersException("Date is null");
        try {
            return userDAO.findUsersByBirthDay(date);
        } catch (GenericDAOException e) {
            logger.error("Error during search for user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Transactional(readOnly = true)
    private boolean isLoginOccupied(String loginName) throws IllegalParametersException, DataAccessException {
        logger.info("Searching for user with login {}", loginName);
        if (loginName == null) throw new IllegalParametersException("Login is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("login", loginName));
        criteria.add(Restrictions.isNull("deleted"));
        try {
            List<User> users = userDAO.findAll(criteria, -1, -1);
            if (CollectionUtils.isNotEmpty(users))
                return true;
            else return false;
        } catch (GenericDAOException e) {
            logger.error("Error during searching for users: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#companyId, 'WarehouseCompany', 'CREATE')")
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
            if (userDTO.getWarehouse() != null) {
                if (userDTO.getWarehouse().getIdWarehouse() != null) {
                    Warehouse warehouse = warehouseService.findWarehouseById(userDTO.getWarehouse().getIdWarehouse().toString());
                    user.setWarehouse(warehouse);
                }
            }
            //todo uncomment
//            if (StringUtils.isNotBlank(user.getPassword())) {
//                user.setPassword(encoder.encode(user.getPassword()));
//            }
            WarehouseCompany warehouseCompany = warehouseCompanyService.findWarehouseCompanyById(companyId.toString());
            user.setWarehouseCompany(warehouseCompany);
            List<Role> roles = new ArrayList<>();
            if (userDTO.getRoles() != null) {
                for (RoleDTO role : userDTO.getRoles()) {
                    if (role.getRole().equals(UserRoleEnum.ROLE_ADMIN.toString())) {
                        if (UserDetailsProvider.getUserDetails().getUser() != null)
                            if (UserDetailsProvider.getUserDetails().getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString()))
                                roles.add(findRoleByName(role.getRole()));
                    } else
                        roles.add(findRoleByName(role.getRole()));
                    user.setRoles(roles);
                }
            } else throw new IllegalParametersException("At least one role has to be selected");
            return userDAO.insert(user);
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }


//    private Warehouse findWarehouseById(Long warehouseId) throws IllegalParametersException, GenericDAOException, ResourceNotFoundException {
//        logger.info("Searching for warehouse with id: {}", warehouseId);
//        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
//        Optional<Warehouse> result = warehouseDAO.findById(warehouseId);
//        if (result.isPresent()) {
//            return result.get();
//        } else throw new ResourceNotFoundException("Warehouse with such id was not found");
//
//    }

//    private WarehouseCompany findWarehouseCompanyById(Long companyId) throws IllegalParametersException, GenericDAOException, ResourceNotFoundException {
//        logger.info("Searching for company with id: {}", companyId);
//        if (companyId == null) throw new IllegalParametersException("Company id is null");
//        Optional<WarehouseCompany> result = warehouseCompanyDAO.findById(companyId);
//        if (result.isPresent()) {
//            return result.get();
//        } else throw new ResourceNotFoundException("Warehouse company with such id was not found");
//
//    }

    @Override
    @Transactional
    public User createSupervisor(Long warehouseId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create supervisor for warehouse with id: {}", warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            Warehouse warehouse = warehouseService.findWarehouseById(warehouseId.toString());
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
                if (role != null)
                    roles.add(findRoleByName(role.getRole()));
                user.setRoles(roles);
                return userDAO.insert(user);
            } else throw new ResourceNotFoundException("Company with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

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
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'UPDATE')")
    public User updateUser(Long id, UserDTO userDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating user with id: {} from DTO: {}", id, userDTO);
        if (id == null || userDTO == null) throw new IllegalParametersException("User DTO or id is null");
        try {
            User user = userDAO.findUserById(id);
            if (user != null) {
                if (userDTO.getWarehouse() != null) {
                    if (userDTO.getWarehouse().getIdWarehouse() != null) {
                        Warehouse warehouse = warehouseService.findWarehouseById(userDTO.getWarehouse().getIdWarehouse().toString());
                        user.setWarehouse(warehouse);
                    }
                }
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
                if (StringUtils.isNotBlank(userDTO.getLogin())) {
                    if (!user.getLogin().equals(userDTO.getLogin()))
                        if (!isLoginOccupied(userDTO.getLogin()))
                            user.setLogin(userDTO.getLogin());
                        else throw new IllegalParametersException("Login name is occupied");


                }
                if (StringUtils.isNotBlank(userDTO.getPassword()))
                    user.setPassword(userDTO.getPassword());
                //todo uncomment
//                if (StringUtils.isNotBlank(userDTO.getPassword())) {
//                    user.setPassword(encoder.encode(userDTO.getPassword()));
//                }

                List<RoleDTO> roles = userDTO.getRoles();
                List<Role> newRoles = new ArrayList<Role>();

                if (roles != null) {
                    for (RoleDTO role : roles) {
                        if (role.getRole().equals(UserRoleEnum.ROLE_ADMIN.toString())) {
                            if (UserDetailsProvider.getUserDetails().getUser() != null)
                                if (UserDetailsProvider.getUserDetails().getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString()))
                                    newRoles.add(findRoleByName(role.getRole()));
                        } else newRoles.add(findRoleByName(role.getRole()));
                    }
                    user.setRoles(newRoles);
                } else {
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
    @PreAuthorize("hasAnyRole('ROLE_OWNER','ROLE_ADMIN','ROLE_SUPERVISOR') and hasPermission(#id, 'WarehouseCompany', 'DELETE')")
    public void deleteUser(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting user with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<User> result = userDAO.findById(id);
            if (result.isPresent()) {
                User user = result.get();
                user.setDeleted(new Date(DateTime.now().toDate().getTime()));
            } else throw new ResourceNotFoundException("User with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting user: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(Long id) throws DataAccessException, IllegalParametersException {
        //todo
        logger.info("Checking if user with id {} exists", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            return userDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            logger.error("Error while determine if user exists: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, UserRoleEnum role) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Checking if user with id {} has role {}", userId, role);
        if (userId == null || role == null) throw new IllegalParametersException("User id or role is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("role", role.toString()));
        try {
            List<Role> fetchedRoles = roleDAO.findAll(criteria, -1, -1);
            if (fetchedRoles.isEmpty())
                throw new ResourceNotFoundException("Role " + role.toString() + " was not found");
            Role foundRole = fetchedRoles.get(0);
            UserDTO user = findUserDTOById(userId);
            if (user == null)
                throw new ResourceNotFoundException("User with such id was not found");
            return user.getRoles().contains(foundRole);
        } catch (GenericDAOException e) {
            logger.error("Error during access to database: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRoles() throws DataAccessException {
        logger.info("Getting roles list");
        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        try {
            List<Role> roleList = roleDAO.findAll(criteria, -1, -1);
            return mapRoleListToDTO(roleList);
        } catch (GenericDAOException e) {
            logger.error("Error during roles list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    private UserDTO mapUserToDTO(User user) {
        Assert.notNull(user, "User is null");
        UserDTO userDTO = UserDTO.buildUserDTO(user);
        userDTO.setRoles(mapRoleListToDTO(user.getRoles()));
        return userDTO;
    }

    private List<UserDTO> mapUserListToDTO(List<User> userList) {
        Assert.notNull(userList, "User list is null");
        List<UserDTO> dtoList = new ArrayList<>();
        for (User user : userList) {
            dtoList.add(mapUserToDTO(user));
        }
        return dtoList;
    }

    private RoleDTO mapRoleToDTO(Role role) {
        Assert.notNull(role, "Role is null");
        return RoleDTO.buildRoleDTO(role);
    }

    private List<RoleDTO> mapRoleListToDTO(List<Role> roleList) {
        Assert.notNull(roleList, "Role list is null");
        List<RoleDTO> dtoList = new ArrayList<>();
        for (Role role : roleList) {
            dtoList.add(mapRoleToDTO(role));
        }
        return dtoList;
    }


}
