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

    List<Warehouse> findWarehousesByCompanyId(Long id, int page, int count) throws DataAccessException, IllegalParametersException;

    Warehouse findWarehouseById(Long id) throws DataAccessException, IllegalParametersException;
    Warehouse findWarehouseOfCompanyById(Long id_warehouse) throws DataAccessException, IllegalParametersException;
        ////Warehouse findWarehouseByLogin(String login) throws DataAccessException;

    List<Warehouse> searchWarehouse(Warehouse searchWarehouse, Long id_user)
            throws DataAccessException, IllegalParametersException;

    Warehouse saveWarehouse(Warehouse warehouse) throws DataAccessException;

    Warehouse updateWarehouse(Long id, Warehouse warehouse)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouse(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isExists(Warehouse warehouse) throws DataAccessException;
}
