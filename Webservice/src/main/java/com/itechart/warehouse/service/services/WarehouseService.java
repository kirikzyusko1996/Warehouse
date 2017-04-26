package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

/**
 * Created by Lenovo on 25.04.2017.
 */
public interface WarehouseService {
    List<Warehouse> findAllWarehouse() throws DataAccessException;

    List<Warehouse> findWarehousesByCompany(Long id) throws DataAccessException;

    Warehouse findWarehouseById(Long id) throws DataAccessException;

    ////Warehouse findWarehouseByLogin(String login) throws DataAccessException;

    ////List<Warehouse> findWarehouseForCompany(Long companyId) throws DataAccessException;

    //Warehouse saveWarehouse(Warehouse warehouse) throws DataAccessException;

    //void deleteWarehouse(Warehouse warehouse) throws DataAccessException;

    //boolean isExists(Warehouse warehouse) throws DataAccessException;
}
