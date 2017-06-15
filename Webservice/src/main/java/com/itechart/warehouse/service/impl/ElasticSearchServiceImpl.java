package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.DriverDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.elasticsearch.ElasticSearchDriver;
import com.itechart.warehouse.service.elasticsearch.ElasticSearchTransportCompany;
import com.itechart.warehouse.service.elasticsearch.ElasticSearchWarehouseCustomerCompany;
import com.itechart.warehouse.service.elasticsearch.SimilarityWrapper;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ElasticSearchService;
import com.itechart.warehouse.service.services.TransportCompanyService;
import com.itechart.warehouse.service.services.WarehouseCustomerCompanyService;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Service Layer for working with elastic search framework
 * Created by Lenovo on 25.05.2017.
 */

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
    private ElasticSearchTransportCompany elasticSearchTransportCompany = new ElasticSearchTransportCompany();
    private ElasticSearchWarehouseCustomerCompany elasticSearchCustomer = new ElasticSearchWarehouseCustomerCompany();
    private ElasticSearchDriver elasticSearchDriver = new ElasticSearchDriver();
    private DriverDAO driverDAO;
    private TransportCompanyService transportCompanyService;
    private WarehouseCustomerCompanyService customerService;

    @Autowired
    public void setDriverDAO(DriverDAO driverDAO) {
        this.driverDAO = driverDAO;
    }

    @Autowired
    public void setTransportCompanyService(TransportCompanyService transportCompanyService) {
        this.transportCompanyService = transportCompanyService;
    }

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service) {
        this.customerService = service;
    }

   /* @PostConstruct
    public void initElasticSearchTransportCompany() throws DataAccessException {
        logger.info("Start init action for es and transport company");
        List<TransportCompany> list = transportCompanyService.findAllTransportCompanies(-1, -1);
        for(TransportCompany tr : list){
            elasticSearchTransportCompany.delete(tr);
            elasticSearchTransportCompany.save(tr);
        }
        logger.info("Complete init for transport comapny (elastic search)");
    }

    @PostConstruct
    public void initElasticSearchCustomer() throws DataAccessException {
        logger.info("Start init action for es and customer companies");
        List<WarehouseCustomerCompany> customers = customerService.findAllWarehouseCustomerCompanies(-1, -1);
        for(WarehouseCustomerCompany customerCompany : customers){
            elasticSearchCustomer.delete(customerCompany);
            elasticSearchCustomer.save(customerCompany);
        }
    }

    @PostConstruct
    public void initElasticSearchDriver()  throws DataAccessException, GenericDAOException,
            IllegalParametersException, ResourceNotFoundException {
        logger.info("Start init action for es and driver");
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Driver.class);
        List<Driver> list = driverDAO.findAll(detachedCriteria, -1, -1);
        for(Driver dr : list){
            Long idCompany = transportCompanyService.findWarehouseCompanyByTransportId(dr.getId()).getIdWarehouseCompany();
            elasticSearchDriver.delete(dr, idCompany);
            elasticSearchDriver.save(dr, idCompany);
        }
        logger.info("Complete init for driver (elastic search)");
    }*/

    @Override
    public List<SimilarityWrapper<TransportCompany>> searchTransportCompany(TransportCompany transportCompany){
        return elasticSearchTransportCompany.search(transportCompany);
    }

    @Override
    public List<SimilarityWrapper<WarehouseCustomerCompany>> searchCustomers(WarehouseCustomerCompany customerCompany) {
        return elasticSearchCustomer.search(customerCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimilarityWrapper<Driver>> searchDriver(Driver driver)throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        Long idCompany = transportCompanyService.findWarehouseCompanyByTransportId(driver.getId()).getIdWarehouseCompany();
        return elasticSearchDriver.search(driver, idCompany);
    }
}
