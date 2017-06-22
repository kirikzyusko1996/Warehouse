package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.dao.CompanyPriceListDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.CompanyPriceListDTO;
import com.itechart.warehouse.entity.CompanyPriceList;
import com.itechart.warehouse.entity.PriceList;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.CompanyFinanceService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class CompanyFinanceServiceImpl implements CompanyFinanceService{
    private CompanyPriceListDAO companyPriceListDAO;

    @Autowired
    public void setCompanyPriceListDAO(CompanyPriceListDAO companyPriceListDAO) {
        this.companyPriceListDAO = companyPriceListDAO;
    }

    private Logger logger = LoggerFactory.getLogger(CompanyFinanceServiceImpl.class);

    @Override
    @Transactional
    public void newPrice(CompanyPriceListDTO priceDTO) throws GenericDAOException {
        logger.info("new price: {} for idWarehouseCompany: {}",
                priceDTO.getPricePerMonth(), priceDTO.getIdWarehouseCompany());
        DetachedCriteria criteria = DetachedCriteria.forClass(CompanyPriceList.class);
        criteria.add(Restrictions.eq("warehouseCompany.idWarehouseCompany",  priceDTO.getIdWarehouseCompany()))
                .add(Restrictions.isNull("endTime"));
        List<CompanyPriceList> priceList = companyPriceListDAO.findAll(criteria, 0, 0);
        Timestamp endTime = new Timestamp((new Date()).getTime());
        CompanyPriceList price;
        if(!priceList.isEmpty()){
            //copy old price into new record with endTime set to current time
            price = priceList.get(0);
            CompanyPriceList insertPrice = new CompanyPriceList();
            insertPrice.setComment(price.getComment());
            insertPrice.setStartTime(price.getStartTime());
            insertPrice.setPricePerMonth(price.getPricePerMonth());
            insertPrice.setWarehouseCompany(price.getWarehouseCompany());
            insertPrice.setEndTime(endTime);
            companyPriceListDAO.insert(insertPrice);
            //update the old price to newPrice
            price.setEndTime(null);
            price.setPricePerMonth(priceDTO.getPricePerMonth());
            price.setStartTime(new Timestamp((new Date()).getTime()));
            price.setComment(priceDTO.getComment());
            companyPriceListDAO.update(price);
        }
        else{
            WarehouseCompany company = new WarehouseCompany();
            company.setIdWarehouseCompany(priceDTO.getIdWarehouseCompany());
            price = new CompanyPriceList();
            price.setPricePerMonth(priceDTO.getPricePerMonth());
            price.setStartTime(new Timestamp((new Date()).getTime()));
            price.setEndTime(null);
            price.setWarehouseCompany(company);
            price.setComment(price.getComment());
            companyPriceListDAO.insert(price);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyPriceList> findAllPrices(int skip, int limit) throws DataAccessException {
        logger.info("Find all prices for every company, skip {}, limit {}", skip, limit);
        DetachedCriteria criteria = DetachedCriteria.forClass(CompanyPriceList.class);
        criteria.addOrder(Order.asc("warehouseCompany.idWarehouseCompany"));
        try {
            return companyPriceListDAO.findAll(criteria, skip, skip+limit);
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<CompanyPriceList> findPricesForWarehouseCompany(Long idWarehouseCompany, int skip, int limit) throws DataAccessException {
        logger.info("Find prices for idWarehouseCompany: {}, skip: {}, limit: {}", idWarehouseCompany, skip, limit);
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(CompanyPriceList.class);
            criteria.add(Restrictions.eq("warehouseCompany.idWarehouseCompany", idWarehouseCompany));
            return companyPriceListDAO.findAll(criteria, 0, 0);
        } catch (GenericDAOException e) {
            logger.error("Error while searching for prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<CompanyPriceList> findCurrentPrices() throws DataAccessException {
        logger.info("Find current price for every company");
        try{
            DetachedCriteria criteria = DetachedCriteria.forClass(CompanyPriceList.class);
            criteria.add(Restrictions.isNull("endTime"));
            return companyPriceListDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during search for current prices: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }
}
