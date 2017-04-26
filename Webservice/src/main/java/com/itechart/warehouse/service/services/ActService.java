package com.itechart.warehouse.service.services;

import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.sql.Date;
import java.util.List;

/**
 * Service for managing acts.
 * Provides basic operations with acts such as searching, creation, updating, deleting.
 */
public interface ActService {
    List<Act> findAllActs() throws DataAccessException;

    Act findActById(Long id) throws DataAccessException;

    List<Act> findActsForGoods(Long goodsId) throws DataAccessException;

    List<Act> findActsForCompany(Long companyId) throws DataAccessException;

    List<Act> findActsForCompanyByType(Long companyId, ActType actType) throws DataAccessException;

    List<Act> findActsForCompanyBetweenDates(Long companyId, Date from, Date to) throws DataAccessException;

    Act saveAct(Act act) throws DataAccessException;

    void deleteAct(Act act) throws DataAccessException;

    boolean isAcExists(Act act) throws DataAccessException;

}
