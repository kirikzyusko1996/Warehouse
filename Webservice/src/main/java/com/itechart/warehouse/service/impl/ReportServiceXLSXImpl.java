package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.LossReportItem;
import com.itechart.warehouse.dto.ReceiptReportItem;
import com.itechart.warehouse.dto.WarehouseReportDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.ReportService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ReportServiceXLSXImpl implements ReportService {
    private GoodsStatusDAO goodsStatusDAO;
    private GoodsStatusNameDAO goodsStatusNameDAO;
    private ActTypeDAO actTypeDAO;
    private ActDAO actDAO;
    private WarehouseDAO warehouseDAO;

    @Autowired
    public void setGoodsStatusDAO(GoodsStatusDAO goodsStatusDAO) {
        this.goodsStatusDAO = goodsStatusDAO;
    }

    @Autowired
    public void setGoodsStatusNameDAO(GoodsStatusNameDAO goodsStatusNameDAO) {
        this.goodsStatusNameDAO = goodsStatusNameDAO;
    }

    @Autowired
    public void setActTypeDAO(ActTypeDAO actTypeDAO) {
        this.actTypeDAO = actTypeDAO;
    }

    @Autowired
    public void setActDAO(ActDAO actDAO) {
        this.actDAO = actDAO;
    }

    @Autowired
    public void setWarehouseDAO(WarehouseDAO warehouseDAO) {
        this.warehouseDAO = warehouseDAO;
    }

    public ReportServiceXLSXImpl(){

    }

    private Logger logger = LoggerFactory.getLogger(ReportServiceXLSXImpl.class);

    @Override
    @Transactional(readOnly = true)
    public void getReceiptReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws DataAccessException {
        logger.info("getReceiptReport of warehouse by id {}, from {} to {}",
                reportDTO.getIdWarehouse(), reportDTO.getStartDate(), reportDTO.getEndDate());
        List<GoodsStatus> goodsStatusList;
        List<ReceiptReportItem> reportItemList = new ArrayList<>();
        Timestamp startTimestamp = new Timestamp(reportDTO.getStartDate().toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(reportDTO.getEndDate().toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria statusNameCriteria = DetachedCriteria.forClass(GoodsStatusName.class);
        statusNameCriteria.add(Restrictions.eq("name", GoodsStatusEnum.STORED.toString()));
        try {
            GoodsStatusName statusName = goodsStatusNameDAO
                    .findAll(statusNameCriteria, 0, 1).get(0);
            //find goodsStatuses for company with specified dates
            DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatus.class);
            criteria.add(Restrictions.eq("goodsStatusName" ,statusName))
                    .createAlias("user", "u")
                    .createAlias("u.warehouseCompany", "w")
                    .add(Restrictions.eq("w.idWarehouseCompany",
                            UserDetailsProvider.getUserDetails().getCompany().getIdWarehouseCompany()))
                    .add(Restrictions.and(Restrictions.ge("date", startTimestamp),
                            Restrictions.le("date", endTimestamp)));
            goodsStatusList = goodsStatusDAO.findAll(criteria, 0, 0);
            Iterator<GoodsStatus> iterator = goodsStatusList.iterator();
            GoodsStatus goodsStatus;
            ReceiptReportItem reportItem = new ReceiptReportItem();
            User user;
            Goods goods;
            while(iterator.hasNext()){
                goodsStatus = iterator.next();
                if(goodsStatus.getGoods().getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse()
                        .equals(reportDTO.getIdWarehouse())){
                    user = goodsStatus.getUser();
                    goods = goodsStatus.getGoods();
                    reportItem.setDate(goodsStatus.getDate());
                    reportItem.setGoodsName(goods.getName());
                    reportItem.setQuantity(goods.getQuantity().toString());
                    reportItem.setUserName(user.getFirstName() + " " + user.getLastName());
                //    reportItem.setShipperName(goods.getIncomingInvoice().getSupplierCompany().getName());
              //      reportItem.setSenderName(goods.getIncomingInvoice().getTransportCompany().getName());
                    reportItemList.add(reportItem);
                }
            }
            //check if report item list is empty
            if(reportItemList.isEmpty()){
                logger.info("No records found for given parameters");
            }

            //generate rows with headers
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFCellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.MEDIUM);
            XSSFSheet sheet = workbook.createSheet("1");
            sheet.setHorizontallyCenter(true);
            XSSFRow reportName = sheet.createRow(0);
            Warehouse warehouse = warehouseDAO.findById(reportDTO.getIdWarehouse()).get();
            reportName.createCell(0).setCellValue("Отчет о поступлении товаров на склад " +
            warehouse.getName() + "  в период с " + reportDTO.getStartDate().toString() + " по "
                    + reportDTO.getEndDate().toString());
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
            XSSFRow header = sheet.createRow(1);
            header.createCell(0).setCellValue("#");
            header.getCell(0).setCellStyle(style);
            header.createCell(1).setCellValue("Дата/Время");
            header.getCell(1).setCellStyle(style);
            header.createCell(2).setCellValue("Наименование");
            header.getCell(2).setCellStyle(style);
            header.createCell(3).setCellValue("Количество");
            header.getCell(3).setCellStyle(style);
            header.createCell(4).setCellValue("Ответственное лицо");
            header.getCell(4).setCellStyle(style);
//            header.createCell(5).setCellValue("Отправитель");
//            header.getCell(5).setCellStyle(style);
//            header.createCell(6).setCellValue("Перевозчик");
//            header.getCell(6).setCellStyle(style);
            //fill cells with data
            for(int i = 0; i < reportItemList.size(); i++){
                XSSFRow row = sheet.createRow(i+2);
                reportItem = reportItemList.get(i);
                row.createCell(0).setCellValue(i+1);
                row.createCell(1).setCellValue(reportItem.getDate());
                row.createCell(2).setCellValue(reportItem.getGoodsName());
                row.createCell(3).setCellValue(reportItem.getQuantity());
                row.createCell(4).setCellValue(reportItem.getUserName());
             //   row.createCell(5).setCellValue(reportItem.getSenderName());
             //   row.createCell(6).setCellValue(reportItem.getShipperName());
                if(i == reportItemList.size() - 1){
                    row.getCell(0).setCellStyle(style);
                    row.getCell(1).setCellStyle(style);
                    row.getCell(2).setCellStyle(style);
                    row.getCell(3).setCellStyle(style);
                    row.getCell(4).setCellStyle(style);
                }
            }
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            workbook.write(outputStream);
        } catch (GenericDAOException e) {
            logger.error("Goods status search error: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }  catch (IOException e) {
            logger.error("Error writing workbook: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getWarehousesLossReport(LocalDate startDate, LocalDate endDate, ServletOutputStream out) throws GenericDAOException {
        logger.info("getWarehousesLossReport from {} to {}", startDate, endDate);
        List<ActType> actTypeList;
        List<Act> actList;
        Map<Long, BigDecimal> mapWarehouseLoss = new HashMap<>();
        List<Warehouse> warehouseList;

        Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(endDate.toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        Criterion restriction1 = Restrictions.eq("name", ActTypeEnum.ACT_OF_LOSS.toString());
        Criterion restriction2 = Restrictions.eq("name", ActTypeEnum.ACT_OF_THEFT.toString());
        criteria.add(Restrictions.or(restriction1, restriction2));
        actTypeList = actTypeDAO.findAll(criteria, 0, 0);

        criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.in("actType", actTypeList))
                .add(Restrictions.and(
                Restrictions.ge("date", startTimestamp),
                Restrictions.le("date", endTimestamp)));
        actList = actDAO.findAll(criteria, 0, 0);
        criteria = DetachedCriteria.forClass(Warehouse.class);

        criteria.add(Restrictions.eq("warehouseCompany", UserDetailsProvider.getUserDetails().getCompany()));

        warehouseList = warehouseDAO.findAll(criteria, 0, 0);
        Iterator<Act> iterator = actList.iterator();
        Act act;
        Long idWarehouse;
        List<Goods> goodsList;
        for(Warehouse warehouse : warehouseList){
            mapWarehouseLoss.put(warehouse.getIdWarehouse(), new BigDecimal("0"));
        }
        while(iterator.hasNext()){
            act = iterator.next();
            goodsList = act.getGoods();
            for(Goods goods : goodsList){
                idWarehouse = goods.getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse();
                if(mapWarehouseLoss.containsKey(idWarehouse)){
                    mapWarehouseLoss.put(idWarehouse, (mapWarehouseLoss.get(idWarehouse)).add(goods.getPrice()));
                }
            }
        }

        if(warehouseList.isEmpty()){
            logger.info("No warehouses registered in database");
        }

        //generate rows with headers
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        XSSFSheet sheet = workbook.createSheet("1");
        XSSFRow reportName = sheet.createRow(0);
        reportName.createCell(0).setCellValue("Отчет об убытках складов" +
               " в период с " + startDate.toString() + " по "
                + endDate.toString());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        sheet.setHorizontallyCenter(true);
        XSSFRow header = sheet.createRow(1);
        header.createCell(0).setCellValue("#");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("Название склада");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("Убытки");
        header.getCell(2).setCellStyle(style);
        //fill cells with data
        BigDecimal totalLoss = new BigDecimal("0");
        for(int i = 0; i < warehouseList.size(); i++){
            XSSFRow row = sheet.createRow(i+2);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(warehouseList.get(i).getName());
            row.createCell(2).setCellValue(
                    mapWarehouseLoss.get(warehouseList.get(i).getIdWarehouse()).toPlainString());
            totalLoss = totalLoss.add(mapWarehouseLoss.get(warehouseList.get(i).getIdWarehouse()));
            if(i == warehouseList.size() - 1){
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);
                row.getCell(2).setCellStyle(style);
            }
        }
        XSSFRow totalRow = sheet.createRow(warehouseList.size() + 2);
        totalRow.createCell(1).setCellValue("ИТОГО УБЫТКОВ:");
        totalRow.createCell(2).setCellValue(totalLoss.toPlainString());
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        try {
            workbook.write(out);
        } catch (IOException e) {
            logger.error("Error generating Warehouses Loss Report: {}", e.getMessage());
        }finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                logger.error("ServletOutputStream error: {}", e.getMessage());
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public void getWarehouseLossReportWithLiableEmployees(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) throws GenericDAOException {
        logger.info("getWarehouse {} Loss Report With Liable Employees from {} to {}",
                reportDTO.getIdWarehouse(), reportDTO.getStartDate(), reportDTO.getEndDate());

        List<ActType> actTypeList;
        List<Act> actList;
        List<LossReportItem> lossReportItemList = new ArrayList<>();
        Timestamp startTimestamp = new Timestamp(reportDTO.getStartDate().toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(reportDTO.getEndDate().toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        Criterion restriction1 = Restrictions.eq("name", ActTypeEnum.ACT_OF_LOSS.toString());
        Criterion restriction2 = Restrictions.eq("name", ActTypeEnum.ACT_OF_THEFT.toString());
        criteria.add(Restrictions.or(restriction1, restriction2));
        actTypeList = actTypeDAO.findAll(criteria, 0, 0);
        criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.in("actType", actTypeList))
                .createAlias("user", "u")
                .add(Restrictions.eq("u.warehouseCompany", UserDetailsProvider.getUserDetails().getCompany()))
                .add(Restrictions.and(
                        Restrictions.ge("date", startTimestamp),
                        Restrictions.le("date", endTimestamp)));
        actList = actDAO.findAll(criteria, 0, 0);
        Iterator<Act> iterator = actList.iterator();
        Act act;
        BigDecimal totalLoss = new BigDecimal("0");
        Map<User, BigDecimal> mapPersonLoss = new HashMap<>();
        List<User> responsiblePersonList = new ArrayList<>();
        while(iterator.hasNext()){
            act = iterator.next();
            for(Goods goods : act.getGoods()) {
                if (goods.getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse().equals(reportDTO.getIdWarehouse())) {
                    if(mapPersonLoss.containsKey(act.getUser())){
                        mapPersonLoss.put(act.getUser(), mapPersonLoss.get(act.getUser()).add(goods.getPrice()));
                    }
                    else{
                        mapPersonLoss.put(act.getUser(), goods.getPrice());
                        responsiblePersonList.add(act.getUser());
                    }
                    totalLoss = totalLoss.add(goods.getPrice());
                }
            }
        }

        if(lossReportItemList.isEmpty()){
            logger.info("No records found for given parameters");
        }

        //generate rows with headers
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        XSSFSheet sheet = workbook.createSheet("1");
        sheet.setHorizontallyCenter(true);
        XSSFRow reportName = sheet.createRow(0);
        Warehouse warehouse = warehouseDAO.findById(reportDTO.getIdWarehouse()).get();
        reportName.createCell(0).setCellValue("Отчет об убытках склада " +
                warehouse.getName() + " по ответственным лицам в период с "
                + reportDTO.getStartDate().toString() + " по " + reportDTO.getEndDate().toString());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        XSSFRow header = sheet.createRow(1);
        header.createCell(0).setCellValue("#");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("Ответственное лицо");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("Стоимость");
        header.getCell(2).setCellStyle(style);
        //fill cells with data
        String name;
        for(int i = 0; i < responsiblePersonList.size(); i++){
            name = responsiblePersonList.get(i).getFirstName() + " " + responsiblePersonList.get(i).getLastName();
            XSSFRow row = sheet.createRow(i+2);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(mapPersonLoss.get(responsiblePersonList.get(i)).toPlainString());
            if(i == responsiblePersonList.size() - 1){
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);
                row.getCell(2).setCellStyle(style);
            }
        }
        XSSFRow totalRow = sheet.createRow(responsiblePersonList.size() + 2);
        totalRow.createCell(1).setCellValue("ИТОГО УБЫТКОВ:");
        totalRow.createCell(2).setCellValue(totalLoss.toPlainString());
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        try {
            workbook.write(outputStream);
        }  catch (IOException e) {
            logger.error("Error generating Warehouse Loss Report With Liable Employees: {}", e.getMessage());
        }
    }

    @Override
    public void getWarehouseProfitReport(WarehouseReportDTO reportDTO, ServletOutputStream outputStream) {
        logger.info("getWarehousesProfitReport for warehouse id {} from {} to {}",
                reportDTO.getIdWarehouse(), reportDTO.getStartDate(), reportDTO.getEndDate());
        Timestamp startTimestamp = new Timestamp(reportDTO.getStartDate().toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(reportDTO.getEndDate().toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
    }
}
