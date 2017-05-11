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

    WarehouseCustomerCompany findWarehouseCustomerCompanyById(Long id) throws DataAccessException;

    WarehouseCustomerCompany findWarehouseCustomerCompanyByName(String name) throws DataAccessException;

    WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompanyDTO customer, WarehouseCompany company) throws DataAccessException;

    WarehouseCustomerCompany updateWarehouseCustomerCompany(String id, WarehouseCustomerCompanyDTO customer)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouseCustomerCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany customer) throws DataAccessException;
}
