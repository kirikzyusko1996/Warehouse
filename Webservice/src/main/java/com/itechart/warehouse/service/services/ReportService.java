package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.joda.time.LocalDate;

import java.io.File;

/**
 * Created by Alexey on 26.04.2017.
 */
public interface ReportService {
    File getReceiptReport(Long idWarehouse, LocalDate startDate, LocalDate endDate) throws DataAccessException;
    File getWarehousesLossReport(LocalDate startDate, LocalDate endDate) throws GenericDAOException;
    File getWarehouseLossReportWithLiableEmployees(Long idWarehouse, LocalDate startDate, LocalDate endDate) throws GenericDAOException;
    File getWarehouseProfitReport(Long idWarehouse, LocalDate startDate, LocalDate endDate);
}
