package com.itechart.warehouse.service.services;


import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

public interface TransportCompanyService {
    List<TransportCompany> findAllTransportCompanies() throws DataAccessException;

    List<TransportCompany> findTransportCompaniesWithOffset(int offset, int limit) throws DataAccessException;

    TransportCompany findTransportCompanyById(Long id) throws DataAccessException;

    TransportCompany findTransportCompanyByName(String name) throws DataAccessException;

    TransportCompany saveTransportCompany(TransportCompany company) throws DataAccessException;

    TransportCompany updateTransportCompany(TransportCompany company) throws DataAccessException;

    void deleteTransportCompany(TransportCompany company) throws DataAccessException;

    boolean TransportCompanyExists(TransportCompany company) throws DataAccessException;
}
