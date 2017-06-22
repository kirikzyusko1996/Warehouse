package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.PriceListDAO;
import com.itechart.warehouse.dao.StorageSpaceTypeDAO;
import com.itechart.warehouse.dao.UserDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.PriceListDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.PriceList;
import com.itechart.warehouse.entity.StorageSpaceType;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.FinanceService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceServiceImpl implements FinanceService{

    private PriceListDAO priceListDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;

    @Autowired
    public void setPriceListDAO(PriceListDAO priceListDAO) {
        this.priceListDAO = priceListDAO;
    }
    @Autowired
    public void setStorageSpaceTypeDAO(StorageSpaceTypeDAO storageSpaceTypeDAO) {
        this.storageSpaceTypeDAO = storageSpaceTypeDAO;
    }

    private Logger logger = LoggerFactory.getLogger(FinanceServiceImpl.class);

    @Override
    @Transactional
    public void newPrice(PriceListDTO priceDTO) throws GenericDAOException {
        logger.info("new price: {} for idStorageSpaceType: {}", priceDTO.getDailyPrice(), priceDTO.getIdStorageSpaceType());
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
        criteria.add(Restrictions.eq("warehouseCompany",  UserDetailsProvider.getUserDetails().getCompany()))
                .add(Restrictions.isNull("endTime"))
                .createAlias("storageSpaceType", "sst")
                .add(Restrictions.eq("sst.idStorageSpaceType", priceDTO.getIdStorageSpaceType()));
        List<PriceList> priceList = priceListDAO.findAll(criteria, 0, 0);
        Timestamp endTime = new Timestamp((new Date()).getTime());
        PriceList price;
        if(!priceList.isEmpty()){
            //copy old price into new record with endTime set to current time
            price = priceList.get(0);
            PriceList insertPrice = new PriceList();
            insertPrice.setComment(price.getComment());
            insertPrice.setStartTime(price.getStartTime());
            insertPrice.setDailyPrice(price.getDailyPrice());
            insertPrice.setWarehouseCompany(price.getWarehouseCompany());
            insertPrice.setStorageSpaceType(price.getStorageSpaceType());
            insertPrice.setEndTime(endTime);
            priceListDAO.insert(insertPrice);
            //update the old price to newPrice
            price.setEndTime(null);
            price.setDailyPrice(priceDTO.getDailyPrice());
            price.setStartTime(new Timestamp((new Date()).getTime()));
            price.setComment(priceDTO.getComment());
            priceListDAO.update(price);
        }
        else{
            price = new PriceList();
            Optional<StorageSpaceType> result = storageSpaceTypeDAO.findById(priceDTO.getIdStorageSpaceType());
            price.setDailyPrice(priceDTO.getDailyPrice());
            price.setStorageSpaceType(result.get());
            price.setStartTime(new Timestamp((new Date()).getTime()));
            price.setEndTime(null);
            price.setWarehouseCompany( UserDetailsProvider.getUserDetails().getCompany());
            price.setComment(price.getComment());
            priceListDAO.insert(price);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceList> findAllPrices(int skip, int limit) throws DataAccessException {
        logger.info("FindAll, skip {}, limit {}", skip, limit);
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
        criteria.add(Restrictions.eq("warehouseCompany",  UserDetailsProvider.getUserDetails().getCompany()));
        try {
            return priceListDAO.findAll(criteria, skip, skip+limit);
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PriceList findPriceById(Long id) throws DataAccessException {
        logger.info("Find price by id: {}", id);
        try {
            Optional<PriceList> result = priceListDAO.findById(id);
            return result.get();
        } catch (GenericDAOException e) {
            logger.error("Error while searching for price: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceList> findPricesForStorageSpaceType(Short idStorageSpaceType, int skip, int limit) throws DataAccessException {
        logger.info("Find prices for idStorageSpaceType: {}, skip: {}, limit: {}", idStorageSpaceType, skip, limit);
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
            criteria.createAlias("storageSpaceType", "sst")
                    .add(Restrictions.eq("sst.idStorageSpaceType", idStorageSpaceType))
                    .add(Restrictions.eq("warehouseCompany", UserDetailsProvider.getUserDetails().getCompany()));
            return priceListDAO.findAll(criteria, 0, 0);
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<PriceList> findPricesByDateForStorageSpaceType(
            Short idStorageSpaceType, LocalDate startDate, LocalDate endDate, int skip, int limit
    ) throws DataAccessException{
        logger.info("Find prices for idStorageSpaceType: {}, skip: {}, limit: {}", idStorageSpaceType, skip, limit);

        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
                Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
                Timestamp endTimestamp = new Timestamp(endDate.toDateTimeAtStartOfDay()
                        .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
                Criterion restriction1 = Restrictions.or(Restrictions.ge("endTime", startTimestamp)
                , Restrictions.isNull("endTime"));
                Criterion restriction2= Restrictions.le("startTime", endTimestamp);
                criteria.add(Restrictions.and(restriction1, restriction2))
                        .createAlias("storageSpaceType", "sst")
                        .add(Restrictions.eq("sst.idStorageSpaceType", idStorageSpaceType))
                        .add(Restrictions.eq("warehouseCompany",  UserDetailsProvider.getUserDetails().getCompany()));
            return priceListDAO.findAll(criteria, skip, limit);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goodsIdList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<PriceList> findPricesByDate(LocalDate startDate, LocalDate endDate, int skip, int limit) throws DataAccessException{
        logger.info("Find prices from {} to {}, skip: {}, limit: {}", startDate, endDate,  skip, limit);
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
            Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
            Timestamp endTimestamp = new Timestamp(endDate.toDateTimeAtStartOfDay()
                    .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
            Criterion restriction1 = Restrictions.or(Restrictions.ge("endTime", startTimestamp)
                    , Restrictions.isNull("endTime"));
            Criterion restriction2= Restrictions.le("startTime", endTimestamp);
            criteria.add(Restrictions.and(restriction1, restriction2))
                    .add(Restrictions.eq("warehouseCompany",  UserDetailsProvider.getUserDetails().getCompany()));
            return priceListDAO.findAll(criteria, skip, limit);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goodsIdList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    public List<PriceList> findCurrentPrices() throws DataAccessException {
        logger.info("Find current prices for company: {}", UserDetailsProvider.getUserDetails().getCompany());
        try{
            DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
            criteria.add(Restrictions.eq("warehouseCompany", UserDetailsProvider.getUserDetails().getCompany()))
                    .add(Restrictions.isNull("endTime"));
            return priceListDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during search for current prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
