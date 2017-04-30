package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.WarehouseReportDTO;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.io.File;

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




    @RequestMapping(value = "/receipt", method = RequestMethod.GET,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response getReceiptReportFile(@Valid @RequestBody WarehouseReportDTO reportDTO){
        logger.info("creating Receipt report for input: {}", reportDTO);
        try {
            File reportFile
                    = reportService.getReceiptReport(
                            reportDTO.getIdWarehouse(), reportDTO.getStartDate(), reportDTO.getEndDate());
            return Response.ok(reportFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"" + reportFile.getName() + "\"" )
                    .build();
        } catch (DataAccessException e) {
            logger.error("Error during during report generation: {}", e.getMessage());
            return Response.serverError().build();
        }
    }

    @RequestMapping(value = "/total_loss", method = RequestMethod.GET,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response getWarehousesLossReportFile(@Valid @RequestBody WarehouseReportDTO reportDTO){
        logger.info("creating warehouses loss report for input: {}", reportDTO);
        try {
            File reportFile
                    = reportService.getWarehousesLossReport(reportDTO.getStartDate(), reportDTO.getEndDate());
            return Response.ok(reportFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"" + reportFile.getName() + "\"" )
                    .build();
        }  catch (GenericDAOException e) {
            logger.error("Report file generation failed: {}", e.getMessage());
            return Response.serverError().build();
        }
    }

    @RequestMapping(value = "/warehouse_loss_with_liable_employees", method = RequestMethod.GET,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response getWarehouseLossReportWithLiableEmplFile(@Valid @RequestBody WarehouseReportDTO reportDTO){
        logger.info("creating warehouses loss report for input: {}", reportDTO);
        try {
            File reportFile
                    = reportService.getWarehouseLossReportWithLiableEmployees(
                            reportDTO.getIdWarehouse(),reportDTO.getStartDate(), reportDTO.getEndDate());
            return Response.ok(reportFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"" + reportFile.getName() + "\"" )
                    .build();
        }  catch (GenericDAOException e) {
            logger.error("Report file generation failed: {}", e.getMessage());
            return Response.serverError().build();
        }
    }
}
