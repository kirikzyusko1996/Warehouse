package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.WarehouseReportDTO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.util.List;

public interface ReportService {
    void getReceiptReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws DataAccessException;
    void getWarehousesLossReport(LocalDate startDate, LocalDate endDate, ServletOutputStream out) throws GenericDAOException, RequestHandlingException;
    void getWarehouseLossReportWithLiableEmployees(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws GenericDAOException, RequestHandlingException;
    void getWarehouseProfitReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream);
    void getSiteOwnerReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream);
}
