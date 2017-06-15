package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.PriceListDTO;
import com.itechart.warehouse.entity.PriceList;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing prices.
 * Provides basic operations with prices such as creation, updating, searching.
 */
public interface FinanceService {
    //create new price or update existent
    void newPrice(PriceListDTO priceDTO) throws GenericDAOException;
    List<PriceList> findAllPrices(int skip, int limit) throws DataAccessException;

    PriceList findPriceById(Long id) throws DataAccessException;

    List<PriceList> findPricesForStorageSpaceType(Short idStorageSpaceType, int skip, int limit) throws DataAccessException;

    List<PriceList> findPricesByDateForStorageSpaceType(Short idStorageSpaceType, LocalDate startDate, LocalDate endDate, int skip, int limit) throws DataAccessException;
    List<PriceList> findPricesByDate(LocalDate startDate, LocalDate endDate, int skip, int limit) throws DataAccessException;
    List<PriceList> findCurrentPrices() throws DataAccessException;
}
