package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.ActDAO;
import com.itechart.warehouse.dao.ActTypeDAO;
import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ActService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of act service.
 */
@Service
public class ActServiceImpl implements ActService {
    private ActDAO actDAO;
    private ActTypeDAO actTypeDAO;
    private UserDAO userDAO;
    private GoodsDAO goodsDAO;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public void setActDAO(ActDAO actDAO) {
        this.actDAO = actDAO;
    }

    @Autowired
    public void setActTypeDAO(ActTypeDAO actTypeDAO) {
        this.actTypeDAO = actTypeDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Act> findAllActs(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find {} acts starting from index {}", maxResults, firstResult);
        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        try {
            return actDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Act findActById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find act by id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Act> result = actDAO.findById(id);
            if (result.isPresent())
                return result.get();
            else throw new ResourceNotFoundException("Act with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during search for act: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActDTO findActDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find act DTO by id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Act> result = actDAO.findById(id);
            if (result.isPresent())
                return mapActToDTO(result.get());
            else throw new ResourceNotFoundException("Act with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during search for act: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ActType> getActTypes() throws DataAccessException {
        logger.info("Getting act types list");
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        try {
            return actTypeDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during act types list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActDTO> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find {} acts starting from index {} by goodsIdList id: {}", maxResults, firstResult, goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        try {
            return mapActsToDTOs(actDAO.findByGoodsId(goodsId));
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Transactional(readOnly = true)
    private ActDTO mapActToDTO(Act act) {
        Assert.notNull(act, "Act is null");
        ActDTO dto = ActDTO.buildStatusDTO(act);
        User user = new User();
        user.setId(act.getUser().getId());
        user.setLastName(act.getUser().getLastName());
        user.setFirstName(act.getUser().getFirstName());
        user.setPatronymic(act.getUser().getPatronymic());
        Hibernate.initialize(act.getGoods());
        dto.setUser(user);
        dto.setGoodsList(act.getGoods());
        return dto;
    }

    private List<ActDTO> mapActsToDTOs(List<Act> acts) {
        Assert.notNull(acts, "Acts list is null");
        List<ActDTO> actDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(acts)) {
            for (Act act : acts) {
                actDTOs.add(mapActToDTO(act));
            }
        }
        return actDTOs;
    }


    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")//todo security check
    public List<ActDTO> findActsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} acts starting from index {} by company id: {}", maxResults, firstResult, companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            return mapActsToDTOs(actDAO.findActsByWarehouseCompanyId(companyId, firstResult, maxResults));
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActsCount(Long warehouseCompanyId) throws DataAccessException, IllegalParametersException {
        logger.info("Get acts count for warehouse company with id: {}", warehouseCompanyId);
        if (warehouseCompanyId == null) throw new IllegalParametersException("Warehouse company id is null");
        try {
            return actDAO.getActsCount(warehouseCompanyId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")//todo security check
    public List<ActDTO> findActsForCompanyByCriteria(Long companyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goodsIdList for company with id {} starting from index {} by criteria: {}", maxResults, companyId, firstResult, actSearchDTO);
        if (actSearchDTO == null || companyId == null)
            throw new IllegalParametersException("Act search DTO or company id is null");
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
            if (StringUtils.isNotBlank(actSearchDTO.getType()))
                criteria.add(Restrictions.eq("actType", findActTypeByName(actSearchDTO.getType())));
            if (actSearchDTO.getFromDate() != null)
                criteria.add(Restrictions.ge("date", actSearchDTO.getFromDate()));
            if (actSearchDTO.getToDate() != null)
                criteria.add(Restrictions.le("date", new Timestamp(new DateTime(actSearchDTO.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime())));
            criteria.createAlias("user", "user");
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorLastName()))
                criteria.add(Restrictions.like("user.lastName", "%" + actSearchDTO.getCreatorLastName() + "%"));
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorFirstName()))
                criteria.add(Restrictions.like("user.firstName", "%" + actSearchDTO.getCreatorFirstName() + "%"));
            if (StringUtils.isNotBlank(actSearchDTO.getCreatorPatronymic()))
                criteria.add(Restrictions.like("user.patronymic", "%" + actSearchDTO.getCreatorPatronymic() + "%"));
            criteria
                    .createCriteria("goods")
                    .createCriteria("incomingInvoice")
                    .createCriteria("warehouse")
                    .createCriteria("warehouseCompany").add(Restrictions.eq("idWarehouseCompany", companyId));
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

            List<ActDTO> dtos = mapActsToDTOs(actDAO.findAll(criteria, firstResult, maxResults));
            if (CollectionUtils.isNotEmpty(dtos)) {
                criteria.setProjection(Projections.rowCount());
                if (dtos.get(0) != null)
                    dtos.get(0).setTotalCount(actDAO.getActsSearchCount(criteria));
            }
            return dtos;
        } catch (GenericDAOException e) {
            logger.error("Error during search for goodsIdList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwner(Long actId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse company of act with id {}", actId);
        if (actId == null) throw new IllegalParametersException("Act id is null");
        Act act = findActById(actId);
        if (act == null)
            throw new ResourceNotFoundException("Act with such id was not found");
        List<Goods> goods = act.getGoods();
        if (!goods.isEmpty())
            if (goods.get(0) != null) {
                Invoice invoice = goods.get(0).getIncomingInvoice();
                if (invoice != null) {
                    Warehouse warehouse = invoice.getWarehouse();
                    if (warehouse != null)
                        return warehouse.getWarehouseCompany();
                }
            }

        throw new ResourceNotFoundException("Warehouse company was not found");
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseOwner(Long actId) throws ResourceNotFoundException, DataAccessException, IllegalParametersException {
        logger.info("Find warehouse company of act with id {}", actId);
        if (actId == null) throw new IllegalParametersException("Act id is null");
        Act act = findActById(actId);
        if (act == null)
            throw new ResourceNotFoundException("Act with such id was not found");
        List<Goods> goods = act.getGoods();
        if (!goods.isEmpty())
            if (goods.get(0) != null) {
                Invoice invoice = goods.get(0).getIncomingInvoice();
                if (invoice != null) {
                    return invoice.getWarehouse();
                }
            }
        throw new ResourceNotFoundException("Warehouse was not found");
    }

    private ActType findActTypeByName(String actTypeName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for act type with name: {}", actTypeName);
        if (actTypeName == null) throw new IllegalParametersException("Act type name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        criteria.add(Restrictions.eq("name", actTypeName));
        List<ActType> fetchedStatusName = actTypeDAO.findAll(criteria, -1, 1);
        if (!fetchedStatusName.isEmpty())
            return fetchedStatusName.get(0);
        else throw new IllegalParametersException("Invalid act type name: " + actTypeName);
    }

    @Override
    @Transactional
    //todo     @PreAuthorize("hasPermission(#companyId, 'Company', 'GET')")
    public Act createAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating act from DTO: {}", actDTO);
        if (actDTO == null) throw new IllegalParametersException("Act DTO is null");
        try {
            Act act = new Act();
            act.setDate(new Timestamp(new Date().getTime()));
            act.setActType(findActTypeByName(actDTO.getType()));
            Optional<User> userResult = userDAO.findById(UserDetailsProvider.getUserDetails().getUserId());
            if (userResult.isPresent()) {
                act.setUser(userResult.get());
            } else {
                throw new ResourceNotFoundException("Authenticated user was not found");
            }
            if (actDTO.getGoodsIdList() != null) {
                setActToGoods(actDTO.getGoodsIdList(), act);
                actDAO.insert(act);
                return act;
            } else throw new IllegalParametersException("List of good's id's is null");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goodsIdList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

    }

    private void setActToGoods(List<Long> goodsList, Act act) throws GenericDAOException {
        for (Long goodsId : goodsList) {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (result.isPresent())
                result.get().addAct(act);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Act', 'UPDATE')")
    public Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating act with id {} from DTO: {}", id, actDTO);
        if (id == null || actDTO == null) throw new IllegalParametersException("Id or act DTO is null");
        try {
            Optional<Act> actResult = actDAO.findById(id);
            if (actResult.isPresent()) {
                Act act = actResult.get();
                act.setActType(findActTypeByName(actDTO.getType()));
                if (actDTO.getGoodsIdList() != null) {
                    setActToGoods(actDTO.getGoodsIdList(), act);
                    return act;
                } else throw new IllegalParametersException("List of good's id's is null");
            } else {
                throw new ResourceNotFoundException("Act with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Act', 'DELETE')")
    public void deleteAct(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting act with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Act> result = actDAO.findById(id);
            if (result.isPresent()) {
                actDAO.delete(result.get());
            } else throw new ResourceNotFoundException("Act with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during deleting act: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public boolean isAcExists(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Checking if act with id {} exists", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            return actDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            logger.error("Error while determine if act exists: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
