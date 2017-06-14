package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.elasticsearch.SimilarityWrapper;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Lenovo on 25.05.2017.
 */
public interface ElasticSearchService {
    List<SimilarityWrapper<TransportCompany>> searchTransportCompany(TransportCompany transportCompany);

    List<SimilarityWrapper<WarehouseCustomerCompany>> searchCustomers(WarehouseCustomerCompany customerCompany);

    List<SimilarityWrapper<Driver>> searchDriver(Driver driver)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
}
