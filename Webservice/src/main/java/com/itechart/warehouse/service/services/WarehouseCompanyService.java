package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.sql.Date;
import java.util.List;
import java.util.Set;

/**
 * Interface for service of warehouse company
 * Created by Lenovo on 25.04.2017.
 */

public interface WarehouseCompanyService {
    WarehouseCompany getWarehouseCompanyById(Long id_warehouse)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<WarehouseCompany> searchWarehouseCompany(WarehouseCompany searchWarehouseCompany)
            throws DataAccessException, IllegalParametersException;

    List<WarehouseCompany> findWarehouseCompany(Long id_user)
            throws DataAccessException, IllegalParametersException;

    List<WarehouseCompany> findAllWarehouseCompany(int page, int count) throws DataAccessException;

    Set<WarehouseCompany> findAllThatUsedForPeriod(Date startDate, Date dueDate)
            throws DataAccessException, IllegalParametersException;

    WarehouseCompany findWarehouseCompanyById(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    User saveWarehouseCompany(WarehouseCompany warehouseCompany, String email)
            throws DataAccessException;

    WarehouseCompany updateWarehouseCompany(Long id, WarehouseCompany company)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteWarehouseCompany(Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isExists(WarehouseCompany warehouseCompany) throws DataAccessException;
}
