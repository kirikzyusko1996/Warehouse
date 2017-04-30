package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.LossReportItem;
import com.itechart.warehouse.dto.ReceiptReportItem;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.ReportService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReportServiceXLSXImpl implements ReportService {
    private GoodsStatusDAO goodsStatusDAO;
    private GoodsStatusNameDAO goodsStatusNameDAO;
    private ActTypeDAO actTypeDAO;
    private ActDAO actDAO;
    private WarehouseDAO warehouseDAO;
    private String tempFileDir;
    private XSSFWorkbook workbook;
    private String propFileName = "report.properties";

    public ReportServiceXLSXImpl(){
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("initialization failed: {}", e.getMessage());
        }
        tempFileDir = properties.getProperty("tempFileDir");
            goodsStatusDAO = new GoodsStatusDAO();
            goodsStatusNameDAO = new GoodsStatusNameDAO();
            actDAO = new ActDAO();
            actTypeDAO = new ActTypeDAO();
            warehouseDAO = new WarehouseDAO();
            workbook = new XSSFWorkbook();
            File tmpdir = new File(tempFileDir);
            if (!(tmpdir.exists())) {
                boolean dirsCreated = tmpdir.mkdirs();
                if (!dirsCreated) {
                    logger.error("Directory creation failed");
                    throw new RuntimeException("temporary directory creation failed");
                }
            }
    }

    private Logger logger = LoggerFactory.getLogger(ReportServiceXLSXImpl.class);

    @Override
    public File getReceiptReport(Long idWarehouse, LocalDate startDate, LocalDate endDate) throws DataAccessException {
        logger.info("getReceiptReport of warehouse by id {}, from {} to {}", idWarehouse, startDate, endDate);
        List<GoodsStatus> goodsStatusList;
        List<ReceiptReportItem> reportItemList = new ArrayList<>();
        String fileName;
        Path filePath=null;

        Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(startDate.toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria statusNameCriteria = DetachedCriteria.forClass(GoodsStatusName.class);
        statusNameCriteria.add(Restrictions.eq("name", GoodsStatusEnum.STORED));



        try {
            int idStatusName = goodsStatusNameDAO
                    .findAll(statusNameCriteria, 0, 1).get(0).getId();

            DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatus.class);
            criteria.add(Restrictions.eq("goodsStatusName.id" ,idStatusName))
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
                if(goodsStatus.getGoods().getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse().equals(idWarehouse)){
                    user = goodsStatus.getUser();
                    goods = goodsStatus.getGoods();
                    reportItem.setDate(goodsStatus.getDate());
                    reportItem.setGoodsName(goods.getName());
                    reportItem.setQuantity(goods.getQuantity().toString());
                    reportItem.setUserName(user.getFirstName() + " " + user.getLastName());
                    reportItem.setShipperName(goods.getIncomingInvoice().getSupplierCompany().getName());
                    reportItem.setSenderName(goods.getIncomingInvoice().getTransportCompany().getName());
                    reportItemList.add(reportItem);
                }
            }
            //check if report item list is empty
            if(reportItemList.isEmpty()){
                logger.info("No records found for given parameters");
            }

            //generate rows with headers
            XSSFSheet sheet = workbook.createSheet("1");
            sheet.setHorizontallyCenter(true);
            XSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("#");
            header.createCell(1).setCellValue("Дата/Время");
            header.createCell(2).setCellValue("Наименование");
            header.createCell(3).setCellValue("Количество");
            header.createCell(4).setCellValue("Ответственное лицо");
            header.createCell(5).setCellValue("Отправитель");
            header.createCell(6).setCellValue("Перевозчик");
            //fill cells with data
            for(int i = 0; i < reportItemList.size(); i++){
                XSSFRow row = sheet.createRow(i+1);
                reportItem = reportItemList.get(i);
                row.createCell(0).setCellValue(i+1);
                row.createCell(1).setCellValue(reportItem.getDate());
                row.createCell(2).setCellValue(reportItem.getGoodsName());
                row.createCell(3).setCellValue(reportItem.getQuantity());
                row.createCell(4).setCellValue(reportItem.getUserName());
                row.createCell(5).setCellValue(reportItem.getSenderName());
                row.createCell(6).setCellValue(reportItem.getShipperName());
            }
            //generate unique file name and save it to default location
            fileName = Integer.toString(ThreadLocalRandom.current().nextInt());
            filePath = Paths.get(tempFileDir, fileName);
            FileOutputStream out = new FileOutputStream(filePath.toFile());
            workbook.write(out);
        } catch (GenericDAOException e) {
            logger.error("Goods status search error: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        } catch (FileNotFoundException e) {
            logger.error("FileOutputStream error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error writing to file: {}", e.getMessage());
        }
        return filePath.toFile();
    }

    @Override
    public File getWarehousesLossReport(LocalDate startDate, LocalDate endDate) throws GenericDAOException {
        logger.info("getWarehousesLossReport from {} to {}", startDate, endDate);
        List<ActType> actTypeList;
        List<Act> actList;
        Map<Long, BigDecimal> mapWarehouseLoss = new HashMap<>();
        List<Warehouse> warehouseList;
        String fileName;
        Path filePath=null;

        Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(startDate.toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        Criterion restriction1 = Restrictions.eq("name", ActTypeEnum.ACT_OF_LOSS);
        Criterion restriction2 = Restrictions.eq("name", ActTypeEnum.ACT_OF_THEFT);
        criteria.add(Restrictions.or(restriction1, restriction2))
                .add(Restrictions.and(
                                Restrictions.ge("date", startTimestamp),
                                Restrictions.le("date", endTimestamp)));
        actTypeList = actTypeDAO.findAll(criteria, 0, 0);
        criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.in("actType", actTypeList));
        actList = actDAO.findAll(criteria, 0, 0);
        criteria = DetachedCriteria.forClass(Warehouse.class);
        warehouseList = warehouseDAO.findAll(criteria, 0, 0);
        Iterator<Act> iterator = actList.iterator();
        Act act;
        Long idWarehouse;
        while(iterator.hasNext()){
            act = iterator.next();
            idWarehouse = act.getGoods().getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse();
            if(mapWarehouseLoss.containsKey(idWarehouse)){
                mapWarehouseLoss.put(idWarehouse, mapWarehouseLoss.get(idWarehouse).add(act.getGoods().getPrice()));
            }
            else{
                mapWarehouseLoss.put(idWarehouse, new BigDecimal("0"));
            }
        }

        if(warehouseList.isEmpty()){
            logger.info("No warehouses registered in database");
        }

        //generate rows with headers
        XSSFSheet sheet = workbook.createSheet("1");
        sheet.setHorizontallyCenter(true);
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("#");
        header.createCell(1).setCellValue("Название склада");
        header.createCell(2).setCellValue("Убытки");
        //fill cells with data
        int lastRow = 0;
        BigDecimal totalLoss = new BigDecimal("0");
        for(int i = 0; i < warehouseList.size(); i++){
            XSSFRow row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(warehouseList.get(i).getName());
            row.createCell(2).setCellValue(
                    mapWarehouseLoss.get(warehouseList.get(i).getIdWarehouse()).toPlainString());
            lastRow = i+1;
            totalLoss = totalLoss.add(mapWarehouseLoss.get(warehouseList.get(i).getIdWarehouse()));
        }
        XSSFRow totalRow = sheet.createRow(lastRow+1);
        totalRow.createCell(1).setCellValue("ИТОГО УБЫТКОВ:");
        totalRow.createCell(2).setCellValue(totalLoss.toPlainString());

        //generate unique file name and save it to default location
        fileName = Integer.toString(ThreadLocalRandom.current().nextInt());
        filePath = Paths.get(tempFileDir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath.toFile());
            workbook.write(out);
        } catch (FileNotFoundException e) {
            logger.error("FileOutputStream error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error writing to file: {}", e.getMessage());
        }
        return filePath.toFile();
    }


    @Override
    public File getWarehouseLossReportWithLiableEmployees(Long idWarehouse, LocalDate startDate, LocalDate endDate) throws GenericDAOException {
        logger.info("getWarehouse {} Loss Report With Liable Employees from {} to {}", idWarehouse, startDate, endDate);
        List<ActType> actTypeList;
        List<Act> actList;
        List<LossReportItem> lossReportItemList = new ArrayList<>();
        String fileName;
        Path filePath=null;

        Timestamp startTimestamp = new Timestamp(startDate.toDateTimeAtStartOfDay().getMillis());
        Timestamp endTimestamp =  new Timestamp(startDate.toDateTimeAtStartOfDay()
                .withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis());
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        Criterion restriction1 = Restrictions.eq("name", ActTypeEnum.ACT_OF_LOSS);
        Criterion restriction2 = Restrictions.eq("name", ActTypeEnum.ACT_OF_THEFT);
        criteria.add(Restrictions.or(restriction1, restriction2))
                .add(Restrictions.and(
                        Restrictions.ge("date", startTimestamp),
                        Restrictions.le("date", endTimestamp)));;
        actTypeList = actTypeDAO.findAll(criteria, 0, 0);
        criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.in("actType", actTypeList));
        actList = actDAO.findAll(criteria, 0, 0);
        Iterator<Act> iterator = actList.iterator();
        Act act;
        LossReportItem lossReportItem = new LossReportItem();
        BigDecimal totalLoss = new BigDecimal("0");
        while(iterator.hasNext()){
            act = iterator.next();
            if(act.getGoods().getCells().get(0).getStorageSpace().getWarehouse().getIdWarehouse().equals(idWarehouse)){
                lossReportItem.setResponsiblePersonName(act.getUser().getFirstName() + " " + act.getUser().getLastName());
                lossReportItem.setGoodsName(act.getGoods().getName());
                lossReportItem.setActType(act.getActType().getName());
                lossReportItem.setActCreationDate(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(act.getDate()));
                lossReportItem.setGoodsCost(act.getGoods().getPrice().toPlainString());
                lossReportItemList.add(lossReportItem);
                totalLoss = totalLoss.add(act.getGoods().getPrice());
            }
        }

        if(lossReportItemList.isEmpty()){
            logger.info("No records found for given parameters");
        }

        //generate rows with headers
        XSSFSheet sheet = workbook.createSheet("1");
        sheet.setHorizontallyCenter(true);
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("#");
        header.createCell(1).setCellValue("Дата/Время составленя акта");
        header.createCell(2).setCellValue("Наименование товара");
        header.createCell(3).setCellValue("Количество");
        header.createCell(4).setCellValue("Стоимость");
        header.createCell(5).setCellValue("Ответственное лицо");
        header.createCell(6).setCellValue("Тип акта");
        //fill cells with data
        int lastRow = 0;
        for(int i = 0; i < lossReportItemList.size(); i++){
            XSSFRow row = sheet.createRow(i+1);
            lossReportItem = lossReportItemList.get(i);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(lossReportItem.getActCreationDate());
            row.createCell(2).setCellValue(lossReportItem.getGoodsName());
            row.createCell(3).setCellValue(lossReportItem.getQuantity());
            row.createCell(4).setCellValue(lossReportItem.getGoodsCost());
            row.createCell(5).setCellValue(lossReportItem.getResponsiblePersonName());
            row.createCell(6).setCellValue(lossReportItem.getActType());
            lastRow = i+1;
        }
        XSSFRow totalRow = sheet.createRow(lastRow+1);
        totalRow.createCell(1).setCellValue("ИТОГО УБЫТКОВ:");
        totalRow.createCell(4).setCellValue(totalLoss.toPlainString());

        //generate unique file name and save it to default location
        fileName = Integer.toString(ThreadLocalRandom.current().nextInt());
        filePath = Paths.get(tempFileDir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath.toFile());
            workbook.write(out);
        } catch (FileNotFoundException e) {
            logger.error("FileOutputStream error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error writing to file: {}", e.getMessage());
        }
        return filePath.toFile();
    }

    @Override
    public File getWarehouseProfitReport(Long idWarehouse, LocalDate startDate, LocalDate endDate) {
        logger.info("getWarehousesProfitReport for id {} from {} to {}", idWarehouse, startDate, endDate);
        return null;
    }
}
