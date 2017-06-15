package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.CompanyPriceListDTO;
import com.itechart.warehouse.entity.CompanyPriceList;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Service for managing prices for companies using warehouse system.
 * Provides basic operations with prices such as creation, updating, searching.
 */
public interface CompanyFinanceService {
    void newPrice(CompanyPriceListDTO priceDTO) throws GenericDAOException;

    List<CompanyPriceList> findAllPrices(int skip, int limit) throws DataAccessException;

    List<CompanyPriceList> findPricesForWarehouseCompany(Long idWarehouseCompany, int skip, int limit) throws DataAccessException;

    List<CompanyPriceList> findCurrentPrices() throws DataAccessException;

}
