package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.ActDAO;
import com.itechart.warehouse.dao.ActTypeDAO;
import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ActService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    public List<Act> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find {} acts starting from index {} by goodsList id: {}", maxResults, firstResult, goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (result.isPresent())
                return result.get().getActs();
            else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Act> findActsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} acts starting from index {} by company id: {}", maxResults, firstResult, companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        try {
            return actDAO.findActsByWarehouseCompanyId(companyId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for acts: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Act> findActsForCompanyByCriteria(Long companyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goodsList for company with id {} starting from index {} by criteria: {}", maxResults, companyId, firstResult, actSearchDTO);
        if (actSearchDTO == null || companyId == null)
            throw new IllegalParametersException("Act search DTO or company id is null");
        try {
            //todo
            DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
            if (actSearchDTO.getType() != null)
                criteria.add(Restrictions.eq("actType", findActTypeByName(actSearchDTO.getType())));
            if (actSearchDTO.getDate() != null)
                criteria.add(Restrictions.eq("date", actSearchDTO.getDate()));
            return actDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goodsList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
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
            if (actDTO.getGoodsList() != null) {
                setActToGoods(actDTO.getGoodsList(), act);
                actDAO.insert(act);
                return act;
            } else throw new IllegalParametersException("List of good's id's is null");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goodsList: {}", e.getMessage());
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
    public Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating act with id {} from DTO: {}", id, actDTO);
        if (id == null || actDTO == null) throw new IllegalParametersException("Id or act DTO is null");
        try {
            Optional<Act> actResult = actDAO.findById(id);
            if (actResult.isPresent()) {
                Act act = actResult.get();
                act.setActType(findActTypeByName(actDTO.getType()));
//                Optional<User> userResult = userDAO.findById(UserDetailsProvider.getUserDetails().getUserId());
//                if (userResult.isPresent()) {
//                    act.setUser(userResult.get());
//                } else {
//                    throw new ResourceNotFoundException("Authenticated user was not found");
//                }
                if (actDTO.getGoodsList() != null) {
                    setActToGoods(actDTO.getGoodsList(), act);
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
