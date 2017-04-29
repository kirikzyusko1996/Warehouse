package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.PriceListDAO;
import com.itechart.warehouse.dao.StorageSpaceTypeDAO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 29.04.2017.
 */
@Service
public class FinanceServiceImpl implements FinanceService{

    private PriceListDAO priceListDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;
    private WarehouseCompanyUserDetails userDetails;
    public FinanceServiceImpl(){
        userDetails = UserDetailsProvider.getUserDetails();
        priceListDAO = new PriceListDAO();
        storageSpaceTypeDAO = new StorageSpaceTypeDAO();
    }

    private Logger logger = LoggerFactory.getLogger(FinanceServiceImpl.class);

    @Override
    public void newPrice(PriceListDTO priceDTO) throws GenericDAOException {
        logger.info("new price: {} for idStorageSpaceType: {}", priceDTO.getDailyPrice(), priceDTO.getIdStorageSpaceType());
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
        criteria.add(Restrictions.eq("warehouseCompany", userDetails.getCompany()))
                .add(Restrictions.isNull("endTime"))
                .add(Restrictions.eq("StorageSpaceType", priceDTO.getIdStorageSpaceType()));
        List<PriceList> priceList = priceListDAO.findAll(criteria, 0, 0);
        Timestamp endTime = new Timestamp((new Date()).getTime());
        PriceList price;
        if(!priceList.isEmpty()){
            //copy old price into new record with endTime set to current time
            price = priceList.get(0);
            price.setEndTime(endTime);
            priceListDAO.insert(price);
            //update the old price to newPrice
            price = priceList.get(0);
            price.setDailyPrice(priceDTO.getDailyPrice());
            price.setComment(priceDTO.getComment());
            priceListDAO.update(price);
        }
        else{
            price = new PriceList();
            Optional<StorageSpaceType> result = storageSpaceTypeDAO.findById(priceDTO.getIdStorageSpaceType());
            price.setDailyPrice(priceDTO.getDailyPrice());
            price.setStorageSpaceType(result.get());
            price.setEndTime(null);
            price.setWarehouseCompany(userDetails.getCompany());
            price.setComment(price.getComment());
            priceListDAO.insert(price);
        }
    }

    @Override
    public List<PriceList> findAllPrices(int skip, int limit) throws DataAccessException {
        logger.info("FindAll, skip {}, limit {}", skip, limit);
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
        criteria.add(Restrictions.eq("warehouseCompany", userDetails.getCompany()));
        try {
            return priceListDAO.findAll(criteria, skip, skip+limit);
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
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
    public List<PriceList> findPricesForStorageSpaceType(Long idStorageSpaceType, int skip, int limit) throws DataAccessException {
        logger.info("Find prices for idStorageSpaceType: {}, skip: {}, limit: {}", idStorageSpaceType, skip, limit);
        try {
            Optional<StorageSpaceType> result = storageSpaceTypeDAO.findById(idStorageSpaceType);
            return result.get().getPriceList();
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<PriceList> findPricesByDate(PriceListDTO priceDTO, int skip, int limit) throws DataAccessException, IllegalParametersException {
        logger.info("Find prices by date: {}, skip: {}, limit: {}", priceDTO.getEndTime(), skip, limit);
        if (priceDTO == null) {
            throw new IllegalParametersException("priceDTO is null");
        }
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(PriceList.class);
            if (priceDTO.getEndTime() != null) {
                Timestamp dayStart = new Timestamp(priceDTO.getEndTime().withTimeAtStartOfDay().getMillis());
                Timestamp dayFinish = new Timestamp(priceDTO.getEndTime()
                        .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
                Criterion restriction1 = Restrictions.ge("endTime", dayStart);
                Criterion restriction2= Restrictions.le("endTime", dayFinish);
                criteria.add(Restrictions.and(restriction1, restriction2));
            }
            return priceListDAO.findAll(criteria, skip, limit);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goodsList: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
