package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Lenovo on 25.04.2017.
 */
public interface WarehouseCompanyService {
    List<WarehouseCompany> findAllWarehouseCompany() throws DataAccessException;

    WarehouseCompany findWarehouseCompanyById(Long id) throws DataAccessException;

    WarehouseCompany saveWarehouseCompany(WarehouseCompany warehouseCompany) throws DataAccessException;

    WarehouseCompany updateWarehouseCompany(String id, WarehouseCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouseCompany(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isExists(WarehouseCompany warehouseCompany) throws DataAccessException;
}
