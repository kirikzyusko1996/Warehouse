package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service for managing acts.
 * Provides basic operations with acts such as searching, creation, updating, deleting.
 */
public interface ActService {
    List<Act> findAllActs(int firstResult, int maxResults) throws DataAccessException;

    Act findActById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<Act> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<Act> findActsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Act> findActsForCompanyByCriteria(Long companyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    WarehouseCompany findWarehouseCompanyOfAct(Long actId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Warehouse findWarehouseOfAct(Long actId) throws ResourceNotFoundException, DataAccessException, IllegalParametersException;

    Act createAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteAct(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isAcExists(Long id) throws DataAccessException, IllegalParametersException;

}
