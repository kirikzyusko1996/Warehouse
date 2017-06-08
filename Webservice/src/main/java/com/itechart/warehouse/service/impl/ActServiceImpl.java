package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.ActDAO;
import com.itechart.warehouse.dao.ActTypeDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ActService;
import com.itechart.warehouse.service.services.GoodsService;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of act service.
 */
@Service
public class ActServiceImpl implements ActService {

    private static final String ERROR_ID_IS_NULL = "Id is null";
    private static final String ERROR_WAREHOUSE_ID_IS_NULL = "Warehouse id is null";

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private ActDAO actDAO;
    private ActTypeDAO actTypeDAO;
    private GoodsService goodsService;
    private WarehouseService warehouseService;
    private UserService userService;

    @Autowired
    @Lazy
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Autowired
    @Lazy
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Autowired
    public void setActDAO(ActDAO actDAO) {
        this.actDAO = actDAO;
    }

    @Autowired
    public void setActTypeDAO(ActTypeDAO actTypeDAO) {
        this.actTypeDAO = actTypeDAO;
    }

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Act> findAllActs(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find acts, first result: {}, max results {}", firstResult, maxResults);
        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        criteria.addOrder(Order.desc("date"));
        try {
            return actDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Act findActById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find act, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        Act act;
        try {
            act = actDAO.getById(id);
            if (act != null) {
                return act;
            } else {
                throw new ResourceNotFoundException("Act with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActDTO findActDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find act DTO, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        Act act = findActById(id);
        if (act != null) {
            return mapActToDTO(act);
        } else {
            throw new ResourceNotFoundException("Act with id " + id + " was not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActType> getActTypes() throws DataAccessException {
        logger.info("Get act types list");
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        try {
            return actTypeDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActDTO> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find acts, first result: {}, max results: {}, goods id: {}", firstResult, maxResults, goodsId);
        if (goodsId == null) {
            throw new IllegalParametersException("Goods id is null");
        }
        try {
            return mapActsToDTOs(actDAO.findByGoodsId(goodsId));
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")//todo security check
    public List<ActDTO> findActsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse id: {}", firstResult, maxResults, warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }
        try {
            return mapActsToDTOs(actDAO.findActsByWarehouseId(warehouseId, firstResult, maxResults));
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get acts count for warehouse with id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }
        try {
            return actDAO.getActsCount(warehouseId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")//todo security check
    public List<ActDTO> findActsForWarehouseByCriteria(Long warehouseId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse id: {}, DTO: {}", firstResult, maxResults, warehouseId, actSearchDTO);
        if (actSearchDTO == null) {
            throw new IllegalParametersException("Act search DTO is null");
        }
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
            if (StringUtils.isNotBlank(actSearchDTO.getType())) {
                criteria.add(Restrictions.eq("actType", findActTypeByName(actSearchDTO.getType())));
            }
            if (actSearchDTO.getFromDate() != null) {
                criteria.add(Restrictions.ge("date", actSearchDTO.getFromDate()));
            }
            if (actSearchDTO.getToDate() != null) {
                criteria.add(Restrictions.le("date", new Timestamp(new DateTime(actSearchDTO.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime())));
            }
            criteria.createAlias("user", "user");
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorLastName())) {
                criteria.add(Restrictions.like("user.lastName", "%" + actSearchDTO.getCreatorLastName() + "%"));
            }
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorFirstName())) {
                criteria.add(Restrictions.like("user.firstName", "%" + actSearchDTO.getCreatorFirstName() + "%"));
            }
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorPatronymic())) {
                criteria.add(Restrictions.like("user.patronymic", "%" + actSearchDTO.getCreatorPatronymic() + "%"));
            }
            criteria
                    .createCriteria("warehouse").add(Restrictions.eq("idWarehouse", warehouseId));
            criteria.add(Restrictions.isNull("deleted"));
            criteria.setProjection(Projections.distinct(Projections.id()));
            DetachedCriteria criteriaWithSubquery = DetachedCriteria.forClass(Act.class);
            criteriaWithSubquery.add(Subqueries.propertyIn("id", criteria));
            criteriaWithSubquery.addOrder(Order.desc("date"));
            return mapActsToDTOs(actDAO.findAll(criteriaWithSubquery, firstResult, maxResults));
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")//todo security check
    public long getCountOfActsForWarehouseByCriteria(Long warehouseId, ActSearchDTO actSearchDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts count, warehouse id: {}, DTO: {}", warehouseId, actSearchDTO);
        if (actSearchDTO == null) {
            throw new IllegalParametersException("Act search DTO is null");
        }
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        if (StringUtils.isNotBlank(actSearchDTO.getType())) {
            criteria.add(Restrictions.eq("actType", findActTypeByName(actSearchDTO.getType())));
        }
        if (actSearchDTO.getFromDate() != null) {
            criteria.add(Restrictions.ge("date", actSearchDTO.getFromDate()));
        }
        if (actSearchDTO.getToDate() != null) {
            criteria.add(Restrictions.le("date", new Timestamp(new DateTime(actSearchDTO.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime())));
        }
        criteria.createAlias("user", "user");
        if (StringUtils.isNotBlank(actSearchDTO.getCreatorLastName())) {
            criteria.add(Restrictions.like("user.lastName", "%" + actSearchDTO.getCreatorLastName() + "%"));
        }
        if (StringUtils.isNotBlank(actSearchDTO.getCreatorFirstName())) {
            criteria.add(Restrictions.like("user.firstName", "%" + actSearchDTO.getCreatorFirstName() + "%"));
        }
        if (StringUtils.isNotBlank(actSearchDTO.getCreatorPatronymic())) {
            criteria.add(Restrictions.like("user.patronymic", "%" + actSearchDTO.getCreatorPatronymic() + "%"));
        }
        criteria
                .createCriteria("warehouse").add(Restrictions.eq("idWarehouse", warehouseId));
        criteria.add(Restrictions.isNull("deleted"));
        criteria.setProjection(Projections.distinct(Projections.id()));
        criteria.setProjection(Projections.rowCount());
        return actDAO.getCount(criteria);

    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwnedBy(Long actId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse company - act owner, act id: {}", actId);
        if (actId == null) throw new IllegalParametersException("Act id is null");

        Act act = findActById(actId);
        if (act == null) {
            throw new ResourceNotFoundException("Act with id" + actId + " was not found");
        }
        if (act.getWarehouse() != null) {
            return act.getWarehouse().getWarehouseCompany();
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseOwnedBy(Long actId) throws ResourceNotFoundException, DataAccessException, IllegalParametersException {
        logger.info("Find warehouse - act owner, act id {}", actId);
        if (actId == null) {
            throw new IllegalParametersException("Act id is null");
        }
        Act act = findActById(actId);
        if (act == null) {
            throw new ResourceNotFoundException("Act with id" + actId + " was not found");
        }
        return act.getWarehouse();
    }

    private ActType findActTypeByName(String actTypeName) throws IllegalParametersException, DataAccessException {
        logger.info("Find act type, name: {}", actTypeName);
        if (actTypeName == null) {
            throw new IllegalParametersException("Act type name is null");
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        criteria.add(Restrictions.eq("name", actTypeName));
        List<ActType> fetchedStatusName;
        try {
            fetchedStatusName = actTypeDAO.findAll(criteria, -1, 1);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        if (!fetchedStatusName.isEmpty()) {
            return fetchedStatusName.get(0);
        } else {
            throw new IllegalParametersException("Invalid act type name: " + actTypeName);
        }
    }


    @Override
    @Transactional
    //todo     @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")
    public Act saveAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create act, DTO: {}", actDTO);
        if (actDTO == null) {
            throw new IllegalParametersException("Act DTO is null");
        }
        try {
            if (CollectionUtils.isNotEmpty(actDTO.getGoodsList())) {
                if (!goodsService.isUpdatable(actDTO.getGoodsList())) {
                    return null;
                }
                Act act = createAct(actDTO);
                List<Goods> goodsList = goodsService.updateAndGetGoodsForAct(actDTO.getType(), actDTO.getGoodsList());
                setActToGoods(goodsList, act);
                actDAO.insert(act);
                return act;
            } else {
                throw new IllegalParametersException("List of goods is empty");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private Act createAct(ActDTO actDTO) throws ResourceNotFoundException, IllegalParametersException, DataAccessException {
        Act act = new Act();
        act.setDate(new Timestamp(new Date().getTime()));
        act.setActType(findActTypeByName(actDTO.getType()));
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        Long userId = userDetails.getUserId();
        User user = userService.findUserById(userId);
        if (user != null) {
            act.setUser(user);
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }
        if (actDTO.getWarehouseId() != null) {
            Warehouse warehouse = warehouseService.findWarehouseById(actDTO.getWarehouseId());
            act.setWarehouse(warehouse);
        }
        act.setNote(actDTO.getNote());
        return act;
    }

    private void setActToGoods(List<Goods> goodsList, Act act) {
        goodsList.stream().filter(goods -> goods != null).forEach(goods ->
                goods.addAct(act)
        );
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Act', 'UPDATE')")
    public Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update act, id {}, DTO: {}", id, actDTO);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }
        if (actDTO == null) {
            throw new IllegalParametersException("Act DTO is null");
        }

        Act act = findActById(id);
        if (act != null) {
            if (actDTO.getType() != null) {
                act.setActType(findActTypeByName(actDTO.getType()));
            }
            if (actDTO.getNote() != null) {
                act.setNote(actDTO.getNote());
            }
        } else {
            throw new ResourceNotFoundException("Act with id " + id + " was not found");
        }
        return act;

    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Act', 'DELETE')")
    public void deleteAct(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete act, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }
        try {
            Act act = actDAO.getById(id);
            if (act != null) {
                act.setDeleted(new java.sql.Date(DateTime.now().toDate().getTime()));
            } else {
                throw new ResourceNotFoundException("Act with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isActExists(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Check if act exists, id {}", id);
        if (id == null) throw new IllegalParametersException(ERROR_ID_IS_NULL);
        try {
            return actDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private ActDTO mapActToDTO(Act act) {
        Assert.notNull(act, "Act is null");
        ActDTO dto = ActDTO.buildActDTO(act);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(act.getUser().getId());
        userDTO.setLastName(act.getUser().getLastName());
        userDTO.setFirstName(act.getUser().getFirstName());
        userDTO.setPatronymic(act.getUser().getPatronymic());
        Hibernate.initialize(act.getGoods());
        dto.setUser(userDTO);
        List<GoodsDTO> goodsDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(act.getGoods())) {
            goodsDTOs.addAll(act.getGoods().stream().map(GoodsDTO::buildGoodsDTO).collect(Collectors.toList()));
        }
        dto.setGoodsList(goodsDTOs);
        Hibernate.initialize(act.getWarehouse());
        if (act.getWarehouse() != null)
            dto.setWarehouseId(act.getWarehouse().getIdWarehouse());
        return dto;
    }

    private List<ActDTO> mapActsToDTOs(List<Act> acts) {
        Assert.notNull(acts, "Acts list is null");
        List<ActDTO> actDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(acts)) {
            actDTOs.addAll(acts.stream().map(this::mapActToDTO).collect(Collectors.toList()));
        }
        return actDTOs;
    }
}
