package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.WarehouseReportDTO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.services.ReportService;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.glassfish.jersey.internal.*;//do not delete

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping(value = "/report")
@Validated
public class ReportController {
    private ReportService reportService;
    private Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    , consumes = "application/json")
    public void getReceiptReportFile(@Valid @RequestBody WarehouseReportDTO reportDTO, HttpServletResponse response) throws IOException {
        logger.info("creating Receipt report for input: {}", reportDTO);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=WarehouseLossReport.xlsx");
            reportService.getReceiptReport(reportDTO,
                    response.getOutputStream());
        }  catch (DataAccessException e) {
            logger.error("Receipt report generation failed: {}", e.getMessage());
        }
    }

    @RequestMapping(value = "/total_loss", method = RequestMethod.POST,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void getWarehousesLossReportFile(@Valid @RequestBody WarehouseReportDTO reportDTO, HttpServletResponse response) throws IOException {
        logger.info("creating warehouses loss report for input: {}", reportDTO);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=WarehouseLossReport.xlsx");
            reportService.getWarehousesLossReport(reportDTO.getStartDate(), reportDTO.getEndDate(), response.getOutputStream());
        }  catch (GenericDAOException e) {
            logger.error("Warehouse Loss Report generation failed: {}", e.getMessage());
        } catch (RequestHandlingException e) {
            logger.error("Loss Report generation failed: {}", e.getMessage());
        }
    }

    @RequestMapping(value = "/warehouse_loss_with_liable_employees", method = RequestMethod.POST,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void getWarehouseLossReportWithLiableEmplFile(@Valid @RequestBody WarehouseReportDTO reportDTO, HttpServletResponse response) throws IOException {
        logger.info("creating warehouses loss report for input: {}", reportDTO);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=WarehouseLossReport.xlsx");
            reportService.getWarehouseLossReportWithLiableEmployees(reportDTO,
                    response.getOutputStream());
        }  catch (Exception e) {
            logger.error("Warehouse loss report with liable employees generation failed: {}", e.getMessage());
        }
    }

    @RequestMapping(value = "/profit", method = RequestMethod.GET,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void getWarehouseProfitReport(@NotEmpty @RequestParam("dateStart") String startDate,
                                         @NotEmpty @RequestParam("dateEnd") String endDate,
                                         @NotEmpty @RequestParam("idWarehouse") Long idWarehouse,
                                         HttpServletResponse response) throws IOException {
        logger.info("creating warehouses profit report for warehouse id {} from {} to {}", idWarehouse, startDate, endDate);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=WarehouseLossReport.xlsx");
            WarehouseReportDTO reportDTO = new WarehouseReportDTO();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            reportDTO.setIdWarehouse(idWarehouse);
            reportDTO.setStartDate(new LocalDate(format.parse(startDate).getTime()));
            reportDTO.setEndDate(new LocalDate(format.parse(endDate).getTime()));
            reportService.getWarehouseProfitReport(reportDTO, response.getOutputStream());
        } catch (ParseException e) {
            logger.error("Can't parse String to LocalDate: {}", e.getMessage());
        }
    }
    @RequestMapping(value = "/siteOwnerReport", method = RequestMethod.POST,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void getSiteOwnerReport(@Valid @RequestBody WarehouseReportDTO reportDTO, HttpServletResponse response) throws IOException {
        logger.info("creating site owner report for input: {}", reportDTO);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=siteOwnerReport.xlsx");
            reportService.getSiteOwnerReport(reportDTO,
                    response.getOutputStream());
        }  catch (Exception e) {
            logger.error("site owner report generation failed: {}", e.getMessage());
        }
    }
}
