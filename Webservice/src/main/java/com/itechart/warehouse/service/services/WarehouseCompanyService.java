package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

/**
 * Created by Lenovo on 25.04.2017.
 */
public interface WarehouseCompanyService {
    List<WarehouseCompany> findAllWarehouseCompany() throws DataAccessException;

    WarehouseCompany findWarehouseCompanyById(Long id) throws DataAccessException;

    WarehouseCompany saveWarehouse(WarehouseCompany warehouseCompany) throws DataAccessException;

    void deleteWarehouse(WarehouseCompany warehouseCompany) throws DataAccessException;

    boolean isExists(WarehouseCompany warehouseCompany) throws DataAccessException;
}
