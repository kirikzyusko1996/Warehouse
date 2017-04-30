package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.PriceListDTO;
import com.itechart.warehouse.entity.PriceList;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;

import java.math.BigDecimal;
import java.util.List;


public interface FinanceService {
    //create new price or update existent
    void newPrice(PriceListDTO priceDTO) throws GenericDAOException;
    List<PriceList> findAllPrices(int skip, int limit) throws DataAccessException;

    PriceList findPriceById(Long id) throws DataAccessException;

    List<PriceList> findPricesForStorageSpaceType(Long idStorageSpaceType, int skip, int limit) throws DataAccessException;

    List<PriceList> findPricesByDate(PriceListDTO priceDTO, int skip, int limit) throws DataAccessException, IllegalParametersException;
}
