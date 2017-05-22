package com.itechart.warehouse.service.services;


import com.itechart.warehouse.dto.TransportCompanyDTO;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

public interface TransportCompanyService {
    List<TransportCompany> findAllTransportCompanies(int page, int count) throws DataAccessException;

    List<TransportCompany> findAllCompaniesForWarehouseCompany(int page, int count, Long idWarehouseCompany) throws DataAccessException, IllegalParametersException;

    TransportCompany findTransportCompanyById(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    TransportCompany findTransportCompanyByName(String name) throws DataAccessException;

    TransportCompany findTransportForCompanyById(Long id, Long warehouseCompanyId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    WarehouseCompany findWarehouseCompanyByTransportId(Long transportId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    TransportCompany saveTransportCompany(TransportCompanyDTO dto, WarehouseCompany company) throws DataAccessException;

    TransportCompany updateTransportCompany(Long id, TransportCompanyDTO dto, Long warehouseCompanyId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteTransportCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean TransportCompanyExists(TransportCompany company) throws DataAccessException;

    TransportCompany mapToCompany(TransportCompanyDTO dto);

    TransportCompanyDTO mapToDto(TransportCompany company);
}
