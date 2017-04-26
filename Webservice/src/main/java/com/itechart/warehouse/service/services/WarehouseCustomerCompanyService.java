package com.itechart.warehouse.service.services;


import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

public interface WarehouseCustomerCompanyService {
    List<WarehouseCustomerCompany> findAllWarehouseCustomerCompanies() throws DataAccessException;

    WarehouseCustomerCompany findWarehouseCustomerCompanyById(Long id) throws DataAccessException;

    WarehouseCustomerCompany findWarehouseCustomerCompanyByName(String name) throws DataAccessException;

    WarehouseCustomerCompany saveWarehouseCustomerCompany(WarehouseCustomerCompany customer) throws DataAccessException;

    WarehouseCustomerCompany updateWarehouseCustomerCompany(WarehouseCustomerCompany customer) throws DataAccessException;

    void deleteWarehouseCustomerCompany(WarehouseCustomerCompany customer) throws DataAccessException;

    boolean warehouseCustomerCompanyExists(WarehouseCustomerCompany customer) throws DataAccessException;
}
