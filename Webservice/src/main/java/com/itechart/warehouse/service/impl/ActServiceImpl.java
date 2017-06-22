package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.ActDAO;
import com.itechart.warehouse.dao.ActTypeDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.UserDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.query.ActSearchCriteria;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of act service.
 */
@Service
public class ActServiceImpl implements ActService {

    private static final String ERROR_ID_IS_NULL = "Id is null";
    private static final String ERROR_WAREHOUSE_ID_IS_NULL = "Warehouse id is null";
    private static final String ERROR_WAREHOUSE_COMPANY_ID_IS_NULL = "Warehouse company id is null";
    private static final String ERROR_ACT_DTO_IS_NULL = "Act DTO is null";

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
        logger.info("Get act types");

        try {
            return actTypeDAO.getActTypes();
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
            List<Act> actList = actDAO.findByGoodsId(goodsId);
            return mapActListToDTOList(actList);
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
            List<Act> actList = actDAO.findActsByWarehouseId(warehouseId, firstResult, maxResults);
            return mapActListToDTOList(actList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActsCountForWarehouse(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get acts count for warehouse with id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            return actDAO.getActsCountByWarehouse(warehouseId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActDTO> findActsForCompany(Long warehouseCompanyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse company id: {}", firstResult, maxResults, warehouseCompanyId);
        if (warehouseCompanyId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        }

        try {
            List<Act> actList = actDAO.findActsByCompanyId(warehouseCompanyId, firstResult, maxResults);
            return mapActListToDTOList(actList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActsCountForCompany(Long warehouseCompanyId) throws DataAccessException, IllegalParametersException {
        logger.info("Get acts count for warehouse company with id: {}", warehouseCompanyId);
        if (warehouseCompanyId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        }

        try {
            return actDAO.getActsCountByCompany(warehouseCompanyId);
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
            ActSearchCriteria criteria = mapActSearchDTOToCriteria(actSearchDTO);
            List<Act> actList = actDAO.findActsForWarehouseByCriteria(warehouseId, criteria, firstResult, maxResults);
            return mapActListToDTOList(actList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private ActSearchCriteria mapActSearchDTOToCriteria(ActSearchDTO dto) throws IllegalParametersException, DataAccessException {
        Assert.notNull(dto, "DTO is null");

        ActSearchCriteria criteria = new ActSearchCriteria();

        if (dto.getType() != null) {
            criteria.setType(findActTypeByName(dto.getType()));
        }
        criteria.setFromDate(dto.getFromDate());
        criteria.setToDate(dto.getToDate());
        criteria.setCreatorLastName(dto.getCreatorLastName());
        criteria.setCreatorFirstName(dto.getCreatorFirstName());
        criteria.setCreatorPatronymic(dto.getCreatorPatronymic());

        return criteria;
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

        ActSearchCriteria criteria = mapActSearchDTOToCriteria(actSearchDTO);
        try {
            return actDAO.getCountOfActsForWarehouseByCriteria(warehouseId, criteria);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActDTO> findActsForWarehouseCompanyByCriteria(Long warehouseCompanyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse company id: {}, DTO: {}", firstResult, maxResults, warehouseCompanyId, actSearchDTO);
        if (actSearchDTO == null) {
            throw new IllegalParametersException("Act search DTO is null");
        }
        if (warehouseCompanyId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        }

        try {
            ActSearchCriteria criteria = mapActSearchDTOToCriteria(actSearchDTO);
            List<Act> actList = actDAO.findActsForWarehouseCompanyByCriteria(warehouseCompanyId, criteria, firstResult, maxResults);
            return mapActListToDTOList(actList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountOfActsForWarehouseCompanyByCriteria(Long warehouseCompanyId, ActSearchDTO actSearchDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Find acts count, warehouse company id: {}, DTO: {}", warehouseCompanyId, actSearchDTO);
        if (actSearchDTO == null) {
            throw new IllegalParametersException("Act search DTO is null");
        }
        if (warehouseCompanyId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        }

        ActSearchCriteria criteria = mapActSearchDTOToCriteria(actSearchDTO);
        try {
            return actDAO.getCountOfActsForWarehouseCompanyByCriteria(warehouseCompanyId, criteria);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwnedBy(Long actId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse company - act owner, act id: {}", actId);
        if (actId == null) {
            throw new IllegalParametersException("Act id is null");
        }

        Act act = findActById(actId);
        if (act != null) {
            try {
                return actDAO.findWarehouseCompanyOfAct(act.getId());
            } catch (GenericDAOException e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        } else {
            throw new ResourceNotFoundException("Act with id" + actId + " was not found");
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
        if (act != null) {
            try {
                return actDAO.findWarehouseOfAct(act.getId());
            } catch (GenericDAOException e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        } else {
            throw new ResourceNotFoundException("Act with id" + actId + " was not found");
        }
    }

    private ActType findActTypeByName(String actTypeName) throws IllegalParametersException, DataAccessException {
        logger.info("Find act type, name: {}", actTypeName);
        if (actTypeName == null) {
            throw new IllegalParametersException("Act type name is null");
        }

        try {
            return actTypeDAO.findActTypeByName(actTypeName);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    //todo     @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")
    public Act saveAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create act, DTO: {}", actDTO);
        if (actDTO == null) {
            throw new IllegalParametersException(ERROR_ACT_DTO_IS_NULL);
        }

        try {
            if (CollectionUtils.isNotEmpty(actDTO.getGoodsList())) {
                if (!goodsService.isUpdatable(actDTO.getGoodsList())) {
                    return null;
                }
                Act act = buildAct(actDTO);
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

    private Act buildAct(ActDTO actDTO) throws ResourceNotFoundException, IllegalParametersException, DataAccessException {
        Assert.notNull(actDTO, ERROR_ACT_DTO_IS_NULL);

        Act act = new Act();

        User user = getAuthenticatedUser();
        if (user != null) {
            act.setUser(user);
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }

        act.setDate(new Timestamp(new Date().getTime()));
        ActType actType = findActTypeByName(actDTO.getType());
        act.setActType(actType);
        act.setNote(actDTO.getNote());

        if (actDTO.getWarehouseId() != null) {
            Warehouse warehouse = warehouseService.findWarehouseById(actDTO.getWarehouseId());
            if (warehouse != null) {
                act.setWarehouse(warehouse);
            } else {
                throw new ResourceNotFoundException("Warehouse with id " + actDTO.getWarehouseId() + " was not found");
            }
        } else {
            throw new IllegalParametersException("Warehouse id is null");
        }

        return act;
    }

    private void setActToGoods(List<Goods> goodsList, Act act) {
        goodsList.stream().filter(Objects::nonNull).forEach(goods ->
                goods.addAct(act)
        );
    }

    private User getAuthenticatedUser() throws ResourceNotFoundException, DataAccessException, IllegalParametersException {
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            Long userId = userDetails.getUserId();
            return userService.findUserById(userId);
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }
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
            throw new IllegalParametersException(ERROR_ACT_DTO_IS_NULL);
        }

        Act act = findActById(id);
        if (act != null) {
            if (actDTO.getType() != null) {
                ActType actType = findActTypeByName(actDTO.getType());
                act.setActType(actType);
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

    private ActDTO mapActToDTO(Act act) {
        Assert.notNull(act, "Act is null");

        ActDTO dto = new ActDTO();

        dto.setId(act.getId());
        dto.setDate(act.getDate());
        dto.setNote(act.getNote());

        if (act.getActType() != null) {
            dto.setType(act.getActType().getName());
        }

        UserDTO userDTO = mapUserToDTO(act.getUser());
        dto.setUser(userDTO);

        List<GoodsDTO> goodsDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(act.getGoods())) {
            goodsDTOs.addAll(act.getGoods().stream().map(this::mapGoodsToDTO).collect(Collectors.toList()));
        }
        dto.setGoodsList(goodsDTOs);

        if (act.getWarehouse() != null) {
            dto.setWarehouseId(act.getWarehouse().getIdWarehouse());
            dto.setWarehouseName(act.getWarehouse().getName());
        }
        return dto;
    }

    private UserDTO mapUserToDTO(User user) {
        Assert.notNull(user, "User is null");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLastName(user.getLastName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setPatronymic(user.getPatronymic());

        return userDTO;
    }


    private GoodsDTO mapGoodsToDTO(Goods goods) {
        Assert.notNull(goods, "Goods is null");

        GoodsDTO dto = new GoodsDTO();

        dto.setId(goods.getId());
        dto.setName(goods.getName());
        dto.setQuantity(goods.getQuantity());
        dto.setWeight(goods.getWeight());
        dto.setPrice(goods.getPrice());
        dto.setStorageType(goods.getStorageType());
        dto.setWeightUnit(goods.getWeightUnit());
        dto.setQuantityUnit(goods.getQuantityUnit());
        dto.setPriceUnit(goods.getPriceUnit());

        return dto;
    }

    private List<ActDTO> mapActListToDTOList(List<Act> acts) {
        Assert.notNull(acts, "Act list is null");

        List<ActDTO> actDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(acts)) {
            actDTOList.addAll(acts.stream().map(this::mapActToDTO).collect(Collectors.toList()));
        }

        return actDTOList;
    }
}
