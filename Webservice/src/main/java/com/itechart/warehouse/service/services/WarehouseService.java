package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Lenovo on 25.04.2017.
 */
public interface WarehouseService {
    List<Warehouse> findAllWarehouse() throws DataAccessException;

    List<Warehouse> findWarehousesByCompanyId(String id) throws DataAccessException, IllegalParametersException;

    Warehouse findWarehouseById(String id) throws DataAccessException, IllegalParametersException;

    ////Warehouse findWarehouseByLogin(String login) throws DataAccessException;

    ////List<Warehouse> findWarehouseForCompany(Long companyId) throws DataAccessException;

    Warehouse saveWarehouse(Warehouse warehouse) throws DataAccessException;

    Warehouse updateWarehouse(String id, Warehouse warehouse)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouse(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isExists(Warehouse warehouse) throws DataAccessException;
}
