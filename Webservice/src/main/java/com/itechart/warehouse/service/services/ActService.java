package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
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

    ActDTO findActDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<ActType> getActTypes() throws DataAccessException;

    List<ActDTO> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<ActDTO> findActsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    long getActsCount(Long warehouseCompanyId) throws DataAccessException, IllegalParametersException;

    List<ActDTO> findActsForCompanyByCriteria(Long companyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    WarehouseCompany findWarehouseCompanyOwnedBy(Long actId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Warehouse findWarehouseOwnedBy(Long actId) throws ResourceNotFoundException, DataAccessException, IllegalParametersException;

    Act createAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteAct(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isActExists(Long id) throws DataAccessException, IllegalParametersException;

}
