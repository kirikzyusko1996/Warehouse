package com.itechart.warehouse.service.services;


import com.itechart.warehouse.dto.WarehouseCustomerCompanyDTO;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

public interface WarehouseCustomerCompanyService {
    List<WarehouseCustomerCompany> findAllWarehouseCustomerCompanies(int page, int count) throws DataAccessException;

    List<WarehouseCustomerCompany> findAllCustomersForWarehouseCompany(int page, int count, Long companyId) throws DataAccessException, IllegalParametersException;

    WarehouseCustomerCompany findCustomerById(Long id) throws DataAccessException;

    WarehouseCustomerCompany findCustomerForCompanyById(Long id, Long companyId) throws DataAccessException;

    WarehouseCustomerCompany findWarehouseCustomerCompanyByName(String name) throws DataAccessException;

    WarehouseCompany findWarehouseCompanyByCustomerId(Long customerId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompanyDTO customer, WarehouseCompany company) throws DataAccessException;

    WarehouseCustomerCompany updateWarehouseCustomerCompany(Long id, WarehouseCustomerCompanyDTO customer, Long companyId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouseCustomerCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany customer) throws DataAccessException;
}
