package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.WarehouseReportDTO;
import com.itechart.warehouse.service.exception.DataAccessException;
import org.joda.time.LocalDate;

import javax.servlet.ServletOutputStream;
import java.io.File;

public interface ReportService {
    void getReceiptReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws DataAccessException;
    void getWarehousesLossReport(LocalDate startDate, LocalDate endDate, ServletOutputStream out) throws GenericDAOException;
    void getWarehouseLossReportWithLiableEmployees(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws GenericDAOException;
    File getWarehouseProfitReport(Long idWarehouse, LocalDate startDate, LocalDate endDate);
}
