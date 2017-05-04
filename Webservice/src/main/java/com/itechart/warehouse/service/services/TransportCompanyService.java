package com.itechart.warehouse.service.services;


import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

public interface TransportCompanyService {
    List<TransportCompany> findAllTransportCompanies(int page, int count) throws DataAccessException;

    TransportCompany findTransportCompanyById(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    TransportCompany findTransportCompanyByName(String name) throws DataAccessException;

    TransportCompany saveTransportCompany(TransportCompany company) throws DataAccessException;

    TransportCompany updateTransportCompany(String id, TransportCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteTransportCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean TransportCompanyExists(TransportCompany company) throws DataAccessException;
}
