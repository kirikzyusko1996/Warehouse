package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.*;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.query.QueryBuilder;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.*;

/**
 * Implementation of goods service.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    private GoodsDAO goodsDAO;
    private GoodsStatusDAO goodsStatusDAO;
    private GoodsStatusNameDAO goodsStatusNameDAO;
    private InvoiceDAO invoiceDAO;
    private UnitDAO unitDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;
    private StorageSpaceDAO storageSpaceDAO;
    private StorageCellDAO storageCellDAO;
    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public void setStorageSpaceDAO(StorageSpaceDAO storageSpaceDAO) {
        this.storageSpaceDAO = storageSpaceDAO;
    }

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Autowired
    public void setGoodsStatusDAO(GoodsStatusDAO goodsStatusDAO) {
        this.goodsStatusDAO = goodsStatusDAO;
    }

    @Autowired
    public void setGoodsStatusNameDAO(GoodsStatusNameDAO goodsStatusNameDAO) {
        this.goodsStatusNameDAO = goodsStatusNameDAO;
    }

    @Autowired
    public void setUnitDAO(UnitDAO unitDAO) {
        this.unitDAO = unitDAO;
    }

    @Autowired
    public void setStorageSpaceTypeDAO(StorageSpaceTypeDAO storageSpaceTypeDAO) {
        this.storageSpaceTypeDAO = storageSpaceTypeDAO;
    }

    @Autowired
    public void setStorageCellDAO(StorageCellDAO storageCellDAO) {
        this.storageCellDAO = storageCellDAO;
    }

    @Autowired
    public void setInvoiceDAO(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findAllGoods(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find {} goods starting from index {}", maxResults, firstResult);
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        try {
            return goodsDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Goods findGoodsById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods by id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            if (result.isPresent())
                return result.get();
            else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsDTO findGoodsDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods DTO by id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            if (result.isPresent())
                return mapGoodsToDTOs(result.get());
            else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<GoodsDTO> findGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods starting from index {} by warehouse id: {}", maxResults, firstResult, warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            List<Goods> goodsList = goodsDAO.findByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOs(goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get users count for warehouse with id: {}", warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return goodsDAO.getGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }


    @Override
    @Transactional
    public List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find {} goods starting from index {} by invoice id: {}", maxResults, firstResult, invoiceId);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        try {
            Optional<Invoice> result = invoiceDAO.findById(invoiceId);
            if (result.isPresent()) {
                Invoice invoice = result.get();
                List<Goods> goods = invoice.getIncomingGoods();
                if (goods.isEmpty()) {
                    goods = invoice.getOutgoingGoods();
                }
                return goods;
            } else throw new ResourceNotFoundException("Invoice with such id was not found ");
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")// TODO: 19.05.2017
    public List<GoodsDTO> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods for warehouse with id {} starting from index {} by criteria: {}", maxResults, warehouseId, firstResult, goodsSearchDTO);
        if (goodsSearchDTO == null || warehouseId == null)
            throw new IllegalParametersException("Goods search DTO or warehouse id is null");

        StringBuilder root = new StringBuilder("SELECT DISTINCT goods FROM Goods goods");
        QueryBuilder builder = new QueryBuilder(root);
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        builder.addJoin("INNER JOIN GoodsStatus status ON status.goods = goods");
        builder.addJoin("INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice");
        builder.addJoin("INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse");

        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("warehouseId", warehouseId);


        if (StringUtils.isNotBlank(goodsSearchDTO.getName())) {
            builder.addRestriction("goods.name LIKE :goodsName");
            queryParameters.put("goodsName", "%" + goodsSearchDTO.getName() + "%");
        }

        if (goodsSearchDTO.getMinQuantity() != null) {
            builder.addRestriction("goods.quantity >= :minGoodsQuantity");
            queryParameters.put("minGoodsQuantity", goodsSearchDTO.getMinQuantity());
        }
        if (goodsSearchDTO.getMaxQuantity() != null) {
            builder.addRestriction("goods.quantity <= :maxGoodsQuantity");
            queryParameters.put("maxGoodsQuantity", goodsSearchDTO.getMaxQuantity());
        }
        if (goodsSearchDTO.getMinWeight() != null) {
            builder.addRestriction("goods.weight >= :minGoodsWeight");
            queryParameters.put("minGoodsWeight", goodsSearchDTO.getMinWeight());
        }
        if (goodsSearchDTO.getMaxWeight() != null) {
            builder.addRestriction("goods.weight <= :maxGoodsWeight");
            queryParameters.put("maxGoodsWeight", goodsSearchDTO.getMaxWeight());
        }

        if (goodsSearchDTO.getMinPrice() != null) {
            builder.addRestriction("goods.price >= :minGoodsPrice");
            queryParameters.put("minGoodsPrice", goodsSearchDTO.getMinPrice());
        }
        if (goodsSearchDTO.getMaxPrice() != null) {
            builder.addRestriction("goods.price <= :maxGoodsPrice");
            queryParameters.put("maxGoodsPrice", goodsSearchDTO.getMaxPrice());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getCurrentStatus())) {
                builder.addRestriction("status_2.goods IS NULL AND status.goodsStatusName = :statusName");
                builder.addJoin("LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date");
                queryParameters.put("statusName", findGoodsStatusNameByName(goodsSearchDTO.getCurrentStatus()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getStorageType())) {
                builder.addRestriction("goods.storageType = :goodsStorageType");
                queryParameters.put("goodsStorageType", findStorageTypeByName(goodsSearchDTO.getStorageType()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for storage type: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getQuantityUnit())) {
                builder.addRestriction("goods.quantityUnit = :goodsQuantityUnit");
                queryParameters.put("goodsQuantityUnit", findUnitByName(goodsSearchDTO.getQuantityUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getWeightUnit())) {
                builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
                queryParameters.put("goodsWeightUnit", findUnitByName(goodsSearchDTO.getWeightUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getPriceUnit())) {
                builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
                queryParameters.put("goodsPriceUnit", findUnitByName(goodsSearchDTO.getPriceUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

        if (goodsSearchDTO.getStatuses() != null) {
            int counter = 3;
            for (GoodsStatusSearchDTO statusDTO : goodsSearchDTO.getStatuses()) {
                try {
                    builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                    if (StringUtils.isNotBlank(statusDTO.getName())) {
                        builder.addRestriction("status_" + counter + ".goodsStatusName = :statusName_" + counter);
                        queryParameters.put("statusName_" + counter, findGoodsStatusNameByName(statusDTO.getName()));
                    }
                    if (statusDTO.getFromDate() != null) {
                        builder.addRestriction("status_" + counter + ".date >= :statusFromDate_" + counter);
                        queryParameters.put("statusFromDate_" + counter, statusDTO.getFromDate());
                    }
                    if (statusDTO.getToDate() != null) {
                        builder.addRestriction("status_" + counter + ".date <= :statusToDate_" + counter);
                        queryParameters.put("statusToDate_" + counter, new Timestamp(new DateTime(statusDTO.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime()));
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserFirstName()) || StringUtils.isNotBlank(statusDTO.getUserLastName()) || StringUtils.isNotBlank(statusDTO.getUserPatronymic())) {
                        builder.addJoin("INNER JOIN User user_" + counter + " ON status_" + counter + ".user = user_" + counter);
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserFirstName())) {
                        builder.addRestriction("user_" + counter + ".firstName LIKE :statusUserFirstName_" + counter);
                        queryParameters.put("statusUserFirstName_" + counter, "%" + statusDTO.getUserFirstName() + "%");
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserLastName())) {
                        builder.addRestriction("user_" + counter + ".lastName LIKE :statusUserLastName_" + counter);
                        queryParameters.put("statusUserLastName_" + counter, "%" + statusDTO.getUserLastName() + "%");
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserPatronymic())) {
                        builder.addRestriction("user_" + counter + ".patronymic LIKE :statusUserPatronymic_" + counter);
                        queryParameters.put("statusUserPatronymic_" + counter, "%" + statusDTO.getUserPatronymic() + "%");
                    }
                    counter++;
                } catch (GenericDAOException e) {
                    logger.error("Error during search for goods status: {}", e.getMessage());
                    throw new DataAccessException(e.getCause());
                }
            }
        }

        try {
            List<Goods> goodsList = goodsDAO.findByQuery(builder.build().toString(), queryParameters, firstResult, maxResults);
            return mapGoodsListToDTOs(goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getGoodsSearchResultCount(Long warehouseId, GoodsSearchDTO goodsSearchDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Get users count for warehouse with id: {} by DTO:", warehouseId, goodsSearchDTO);
        if (warehouseId == null || goodsSearchDTO == null)
            throw new IllegalParametersException("Warehouse id or DTO is null");
        StringBuilder root = new StringBuilder("SELECT count(*) FROM Goods goods");
        QueryBuilder builder = new QueryBuilder(root);
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        builder.addJoin("INNER JOIN GoodsStatus status ON status.goods = goods");
        builder.addJoin("INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice");
        builder.addJoin("INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse");

        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("warehouseId", warehouseId);


        if (StringUtils.isNotBlank(goodsSearchDTO.getName())) {
            builder.addRestriction("goods.name LIKE :goodsName");
            queryParameters.put("goodsName", "%" + goodsSearchDTO.getName() + "%");
        }

        if (goodsSearchDTO.getMinQuantity() != null) {
            builder.addRestriction("goods.quantity >= :minGoodsQuantity");
            queryParameters.put("minGoodsQuantity", goodsSearchDTO.getMinQuantity());
        }
        if (goodsSearchDTO.getMaxQuantity() != null) {
            builder.addRestriction("goods.quantity <= :maxGoodsQuantity");
            queryParameters.put("maxGoodsQuantity", goodsSearchDTO.getMaxQuantity());
        }
        if (goodsSearchDTO.getMinWeight() != null) {
            builder.addRestriction("goods.weight >= :minGoodsWeight");
            queryParameters.put("minGoodsWeight", goodsSearchDTO.getMinWeight());
        }
        if (goodsSearchDTO.getMaxWeight() != null) {
            builder.addRestriction("goods.weight <= :maxGoodsWeight");
            queryParameters.put("maxGoodsWeight", goodsSearchDTO.getMaxWeight());
        }

        if (goodsSearchDTO.getMinPrice() != null) {
            builder.addRestriction("goods.price >= :minGoodsPrice");
            queryParameters.put("minGoodsPrice", goodsSearchDTO.getMinPrice());
        }
        if (goodsSearchDTO.getMaxPrice() != null) {
            builder.addRestriction("goods.price <= :maxGoodsPrice");
            queryParameters.put("maxGoodsPrice", goodsSearchDTO.getMaxPrice());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getCurrentStatus())) {
                builder.addRestriction("status_2.goods IS NULL AND status.goodsStatusName = :statusName");
                builder.addJoin("LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date");
                queryParameters.put("statusName", findGoodsStatusNameByName(goodsSearchDTO.getCurrentStatus()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getStorageType())) {
                builder.addRestriction("goods.storageType = :goodsStorageType");
                queryParameters.put("goodsStorageType", findStorageTypeByName(goodsSearchDTO.getStorageType()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for storage type: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getQuantityUnit())) {
                builder.addRestriction("goods.quantityUnit = :goodsQuantityUnit");
                queryParameters.put("goodsQuantityUnit", findUnitByName(goodsSearchDTO.getQuantityUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getWeightUnit())) {
                builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
                queryParameters.put("goodsWeightUnit", findUnitByName(goodsSearchDTO.getWeightUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getPriceUnit())) {
                builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
                queryParameters.put("goodsPriceUnit", findUnitByName(goodsSearchDTO.getPriceUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

        if (goodsSearchDTO.getStatuses() != null) {
            int counter = 3;
            for (GoodsStatusSearchDTO statusDTO : goodsSearchDTO.getStatuses()) {
                try {
                    builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                    if (StringUtils.isNotBlank(statusDTO.getName())) {
                        builder.addRestriction("status_" + counter + ".goodsStatusName = :statusName_" + counter);
                        queryParameters.put("statusName_" + counter, findGoodsStatusNameByName(statusDTO.getName()));
                    }
                    if (statusDTO.getFromDate() != null) {
                        builder.addRestriction("status_" + counter + ".date >= :statusFromDate_" + counter);
                        queryParameters.put("statusFromDate_" + counter, statusDTO.getFromDate());
                    }
                    if (statusDTO.getToDate() != null) {
                        builder.addRestriction("status_" + counter + ".date <= :statusToDate_" + counter);
                        queryParameters.put("statusToDate_" + counter, new Timestamp(new DateTime(statusDTO.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime()));
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserFirstName()) || StringUtils.isNotBlank(statusDTO.getUserLastName()) || StringUtils.isNotBlank(statusDTO.getUserPatronymic())) {
                        builder.addJoin("INNER JOIN User user_" + counter + " ON status_" + counter + ".user = user_" + counter);
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserFirstName())) {
                        builder.addRestriction("user_" + counter + ".firstName LIKE :statusUserFirstName_" + counter);
                        queryParameters.put("statusUserFirstName_" + counter, "%" + statusDTO.getUserFirstName() + "%");
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserLastName())) {
                        builder.addRestriction("user_" + counter + ".lastName LIKE :statusUserLastName_" + counter);
                        queryParameters.put("statusUserLastName_" + counter, "%" + statusDTO.getUserLastName() + "%");
                    }
                    if (StringUtils.isNotBlank(statusDTO.getUserPatronymic())) {
                        builder.addRestriction("user_" + counter + ".patronymic LIKE :statusUserPatronymic_" + counter);
                        queryParameters.put("statusUserPatronymic_" + counter, "%" + statusDTO.getUserPatronymic() + "%");
                    }
                    counter++;
                } catch (GenericDAOException e) {
                    logger.error("Error during search for goods status: {}", e.getMessage());
                    throw new DataAccessException(e.getCause());
                }
            }
        }

        try {
            return goodsDAO.getCountByQuery(builder.build().toString(), queryParameters);
        } catch (GenericDAOException e) {
            logger.error("Error during getting count: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }


    private List<GoodsDTO> mapGoodsListToDTOs(List<Goods> goodsList) {
        List<GoodsDTO> dtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Goods goods : goodsList) {
                dtos.add(mapGoodsToDTOs(goods));
            }
        }
        return dtos;
    }

    private GoodsDTO mapGoodsToDTOs(Goods goods) {
        Assert.notNull(goods, "Goods is null");
        GoodsDTO dto = GoodsDTO.buildGoodsDTO(goods);
        try {
            GoodsStatus status = goodsDAO.findGoodsCurrentStatus(goods.getId());
            if (status != null)
                dto.setStatus(GoodsStatusDTO.buildStatusDTO(status));
        } catch (GenericDAOException e) {
            logger.error("Error getting current status: {}", e);
        }
        List<StorageCellDTO> cellDTOs = new ArrayList<>();
        for (StorageCell cell : goods.getCells()) {
            StorageCellDTO cellDTO = new StorageCellDTO();
            cellDTO.setIdGoods(goods.getId());
            cellDTO.setIdStorageCell(cell.getIdStorageCell());
            cellDTO.setIdStorageSpace(cell.getStorageSpace().getIdStorageSpace());
            cellDTO.setNumber(cell.getNumber());
            cellDTOs.add(cellDTO);
        }
        dto.setCells(cellDTOs);
        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Goods> findGoodsForWarehouseByStatus(Long warehouseId, String statusName, int firstResult,
                                                     int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods for warehouse with id {} starting from index {}", maxResults, warehouseId, firstResult);
        try {
            if (warehouseId == null || statusName == null)
                throw new IllegalParametersException("Status name or warehouse id is null");
            GoodsStatusName status = findGoodsStatusNameByName(statusName);
            if (status == null)
                throw new IllegalParametersException("Status with name" + statusName + "was not found");
            return goodsDAO.findByWarehouseIdAndCurrentStatus(warehouseId, status, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during retrieval of goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsStatusDTO> findStatusesOfGoods(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find all statuses of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Optional<Goods> result = null;
        try {
            List<GoodsStatus> statuses = goodsStatusDAO.findByGoodsId(goodsId);
            return mapGoodsStatusesToDTOs(statuses);
        } catch (GenericDAOException e) {
            logger.error("Error during retrieval of goods statuses: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    private GoodsStatusDTO mapGoodsStatusToDTO(GoodsStatus status) {
        Assert.notNull(status, "Status is null");
        GoodsStatusDTO dto = GoodsStatusDTO.buildStatusDTO(status);
        User user = new User();
        user.setId(status.getUser().getId());
        user.setLastName(status.getUser().getLastName());
        user.setFirstName(status.getUser().getFirstName());
        user.setPatronymic(status.getUser().getPatronymic());
        dto.setUser(user);
        return dto;
    }

    private List<GoodsStatusDTO> mapGoodsStatusesToDTOs(List<GoodsStatus> statuses) {
        Assert.notNull(statuses, "Statuses is null");
        List<GoodsStatusDTO> statusDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(statuses)) {
            for (GoodsStatus status : statuses) {
                statusDTOs.add(mapGoodsStatusToDTO(status));
            }
        }
        return statusDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsStatus findGoodsCurrentStatus(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find current status of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        try {
            return goodsStatusDAO.findCurrentByGoodsId(goodsId);
        } catch (GenericDAOException e) {
            logger.error("Error during retrieval of goods current status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseOwner(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Goods goods = findGoodsById(goodsId);
        return goods.getIncomingInvoice().getWarehouse();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwner(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Goods goods = findGoodsById(goodsId);
        return goods.getIncomingInvoice().getWarehouse().getWarehouseCompany();
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Goods', 'UPDATE')")
    public Goods updateGoods(Long id, GoodsDTO goodsDTO) throws
            DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Updating goods with id {} from DTO: {}", id, goodsDTO);
        if (id == null || goodsDTO == null) throw new IllegalParametersException("Id or goods DTO is null");
        try {
            Goods goodsToUpdate = findGoodsById(id);
            if (goodsToUpdate != null) {
                if (StringUtils.isNotBlank(goodsDTO.getName()))
                    goodsToUpdate.setName(goodsDTO.getName());
                else throw new IllegalParametersException("Field name can not be empty");
                if (goodsDTO.getQuantity() != null)
                    goodsToUpdate.setQuantity(goodsDTO.getQuantity());
                else throw new IllegalParametersException("Field quantity can not be empty");
                if (goodsDTO.getWeight() != null)
                    goodsToUpdate.setWeight(goodsDTO.getWeight());
                else throw new IllegalParametersException("Field weight can not be empty");
                if (goodsDTO.getPrice() != null)
                    goodsToUpdate.setPrice(goodsDTO.getPrice());
                else throw new IllegalParametersException("Field price can not be empty");
                if (goodsDTO.getPriceUnit() != null) {
                    if (goodsDTO.getPriceUnit().getName() != null)
                        goodsToUpdate.setPriceUnit(findUnitByName(goodsDTO.getPriceUnit().getName()));
                } else throw new IllegalParametersException("Price unit can not be empty");
                if (goodsDTO.getQuantityUnit() != null) {
                    if (goodsDTO.getQuantityUnit().getName() != null)
                        goodsToUpdate.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnit().getName()));
                } else throw new IllegalParametersException("Quantity unit can not be empty");
                if (goodsDTO.getWeightUnit() != null) {
                    if (goodsDTO.getWeightUnit().getName() != null)
                        goodsToUpdate.setWeightUnit(findUnitByName(goodsDTO.getWeightUnit().getName()));
                } else throw new IllegalParametersException("Weight unit can not be empty");
                if (goodsDTO.getStorageType() != null) {
                    if (goodsDTO.getStorageType().getName() != null)
                        goodsToUpdate.setStorageType(findStorageTypeByName(goodsDTO.getStorageType().getName()));
                } else throw new IllegalParametersException("Storage type can not be empty");
                Goods savedGoods = goodsDAO.update(goodsToUpdate);

                if (goodsDTO.getStatus() != null) {
                    if (StringUtils.isNotBlank(goodsDTO.getStatus().getName())) {
                        GoodsStatus goodsStatus = new GoodsStatus();
                        goodsStatus.setGoods(savedGoods);
                        goodsStatus.setDate(new Timestamp(new Date().getTime()));
                        goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
                        goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(goodsDTO.getStatus().getName()));
                        goodsStatusDAO.insert(goodsStatus);
                    }
                }
                return savedGoods;

            } else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }


    private Unit findUnitByName(String unitName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for unit with name: {}", unitName);
        if (unitName == null) throw new IllegalParametersException("Unit name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Unit.class);
        criteria.add(Restrictions.eq("name", unitName));
        List<Unit> fetchedUnits = unitDAO.findAll(criteria, -1, 1);
        if (!fetchedUnits.isEmpty())
            return fetchedUnits.get(0);
        else throw new IllegalParametersException("Invalid unit name: " + unitName);
    }

    private StorageSpaceType findStorageTypeByName(String spaceTypeName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for storage space type with name: {}", spaceTypeName);
        if (spaceTypeName == null) throw new IllegalParametersException("Storage space type name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        criteria.add(Restrictions.eq("name", spaceTypeName));
        List<StorageSpaceType> fetchedSpaceType = storageSpaceTypeDAO.findAll(criteria, -1, 1);
        if (!fetchedSpaceType.isEmpty())
            return fetchedSpaceType.get(0);
        else throw new IllegalParametersException("Invalid storage space type name: " + spaceTypeName);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#invoiceId, 'Invoice', 'GET')")
    public Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating goods for invoice with id {} from DTO: {}", invoiceId, goodsDTO);
        if (invoiceId == null || goodsDTO == null)
            throw new IllegalParametersException("Invoice id or goods DTO is null");
        try {
            Goods goods = new Goods();
            if (StringUtils.isNotBlank(goodsDTO.getName()))
                goods.setName(goodsDTO.getName());
            else throw new IllegalParametersException("Field name can not be empty");
            if (goodsDTO.getPrice() != null)
                goods.setPrice(goodsDTO.getPrice());
            else throw new IllegalParametersException("Field price can not be empty");
            if (goodsDTO.getQuantity() != null)
                goods.setQuantity(goodsDTO.getQuantity());
            else throw new IllegalParametersException("Field quantity can not be empty");
            if (goodsDTO.getWeight() != null)
                goods.setWeight(goodsDTO.getWeight());
            else throw new IllegalParametersException("Field weight can not be empty");

            if (goodsDTO.getPriceUnit() != null) {
                if (goodsDTO.getPriceUnit().getName() != null)
                    goods.setPriceUnit(findUnitByName(goodsDTO.getPriceUnit().getName()));
            } else throw new IllegalParametersException("Price unit can not be empty");
            if (goodsDTO.getQuantityUnit() != null) {
                if (goodsDTO.getQuantityUnit().getName() != null)
                    goods.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnit().getName()));
            } else throw new IllegalParametersException("Quantity unit can not be empty");
            if (goodsDTO.getWeightUnit() != null) {
                if (goodsDTO.getWeightUnit().getName() != null)
                    goods.setWeightUnit(findUnitByName(goodsDTO.getWeightUnit().getName()));
            } else throw new IllegalParametersException("Weight unit can not be empty");
            if (goodsDTO.getStorageType() != null) {
                if (goodsDTO.getStorageType().getName() != null)
                    goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageType().getName()));
            } else throw new IllegalParametersException("Storage type can not be empty");
            Invoice invoice = findInvoiceById(invoiceId);
            if (invoice != null)
                goods.setIncomingInvoice(invoice);
            else throw new ResourceNotFoundException("Invoice with such id was not found");
            Goods savedGoods = goodsDAO.insert(goods);
            if (savedGoods != null) {
                GoodsStatus goodsStatus = new GoodsStatus();
                goodsStatus.setGoods(savedGoods);
                goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(GoodsStatusEnum.REGISTERED.toString()));
                goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
                goodsStatus.setDate(new Timestamp(new Date().getTime()));
            }
            return savedGoods;
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    /**
     * Splits goods entry into two new: one affected by act, other one - not affected.
     * Quantity, weight and price split between two entries.
     *
     * @param actType   type of act, used to set status of affected by act goods.
     * @param goodsList list of goods affected by act and have to be split.
     * @return list of newly created goods entries, affected by act.
     */
    @Override
    public List<Goods> splitGoodsForAct(String actType, List<Goods> goodsList) throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Splitting goods to become part of new act with type {} for list of goods: {}", actType, goodsList);
        if (actType == null || goodsList == null)
            throw new IllegalParametersException("Act type or goods list is null");
        List<Goods> returnedList = new ArrayList<>();
        try {
            String statusName = null;
            if (actType != null) {
                switch (ActTypeEnum.valueOf(actType)) {
                    case ACT_OF_LOSS:
                        statusName = GoodsStatusEnum.LOST_BY_WAREHOUSE_COMPANY.toString();
                        break;
                    case ACT_OF_THEFT:
                        statusName = GoodsStatusEnum.STOLEN.toString();
                        break;
                    case WRITE_OFF_ACT:
                        statusName = GoodsStatusEnum.RECYCLED.toString();
                        break;
                    case MISMATCH_ACT:
                        statusName = GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH.toString();
                        break;
                }
            }

            for (Goods goods : goodsList) {
                if (goods != null) {
                    Optional<Goods> goodsResult = goodsDAO.findById(goods.getId());
                    if (goodsResult.isPresent()) {
                        Goods initialGoods = goodsResult.get();
                        Goods goodsInAct = new Goods(initialGoods);
                        goodsInAct.setStatuses(new ArrayList<>());//emptying statuses for affected by act goods
                        goodsInAct.setCells(new ArrayList<>());
                        initialGoods.setActs(new ArrayList<>());//emptying acts for not affected by act goods
                        if (goods.getQuantity().compareTo(initialGoods.getQuantity()) <= 0) {
                            goodsInAct.setQuantity(goods.getQuantity());
                            initialGoods.setQuantity(initialGoods.getQuantity().subtract(goods.getQuantity()));
                        } else
                            throw new IllegalParametersException("Quantity covered by act can not be more than initial value");


                        if (goods.getWeight().compareTo(initialGoods.getWeight()) <= 0) {
                            goodsInAct.setWeight(goods.getWeight());
                            initialGoods.setWeight(initialGoods.getWeight().subtract(goods.getWeight()));
                        } else
                            throw new IllegalParametersException("Weight covered by act can not be more than initial value");

                        if (goods.getPrice().compareTo(initialGoods.getPrice()) <= 0) {
                            goodsInAct.setPrice(goods.getPrice());
                            initialGoods.setPrice(initialGoods.getPrice().subtract(goods.getPrice()));
                        } else
                            throw new IllegalParametersException("Weight covered by act can not be more than initial value");

                        Goods returnedGoods = goodsDAO.insert(goodsInAct);
                        if (returnedGoods != null) {
                            GoodsStatus goodsStatus = new GoodsStatus();
                            goodsStatus.setGoods(goods);
                            goodsStatus.setDate(new Timestamp(new Date().getTime()));
                            goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
                            goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(statusName));
                            goodsStatusDAO.insert(goodsStatus);
                        }
                        returnedList.add(returnedGoods);


                    }
                }
            }
            return returnedList;
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public List<Goods> createGoodsBatch(Long invoiceId, List<GoodsDTO> goodsDtoList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Creating batch of goods {} for invoice with id: {}", goodsDtoList, invoiceId);
        if (invoiceId == null || goodsDtoList == null)
            throw new IllegalParametersException("Invoice id or goods list is null");
        List<Goods> goodsList = new ArrayList<>();
        for (GoodsDTO dto : goodsDtoList) {
            goodsList.add(createGoods(invoiceId, dto));
        }
        return goodsList;
    }

    private Invoice findInvoiceById(Long invoiceId) throws GenericDAOException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Searching for invoice with id: {}", invoiceId);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        Optional<Invoice> result = invoiceDAO.findById(invoiceId);
        if (result.isPresent())
            return result.get();
        else throw new ResourceNotFoundException("Invoice was not found");
    }

    private User findUserById(Long userId) throws GenericDAOException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Searching for user with id: {}", userId);
        if (userId == null) throw new IllegalParametersException("User id is null");
        Optional<User> result = userDAO.findById(userId);
        if (result.isPresent())
            return result.get();
        else throw new ResourceNotFoundException("User was not found");
    }

    private GoodsStatusName findGoodsStatusNameByName(String goodsStatusNameName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for goods status name with name: {}", goodsStatusNameName);
        if (goodsStatusNameName == null) throw new IllegalParametersException("Goods status name name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        criteria.add(Restrictions.eq("name", goodsStatusNameName));
        List<GoodsStatusName> fetchedStatusName = goodsStatusNameDAO.findAll(criteria, -1, 1);
        if (!fetchedStatusName.isEmpty())
            return fetchedStatusName.get(0);
        else throw new IllegalParametersException("Invalid status name: " + goodsStatusNameName);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Goods', 'DELETE')")
    public void deleteGoods(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting goods with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            if (result != null) {
                removeGoodsFromStorage(id);
                goodsDAO.delete(result.get());
            } else {
                throw new ResourceNotFoundException("Goods with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during deleting goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isGoodsExists(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Checking if goods with id {} exists", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            return goodsDAO.isExistsEntity(id);
        } catch (GenericDAOException e) {
            logger.error("Error while determine if goods exists: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Setting status: {} to goods with id {}", goodsStatusDTO, goodsId);
        if (goodsId == null || goodsStatusDTO == null)
            throw new IllegalParametersException("Goods status DTO or goods id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (result.isPresent()) {
                Goods goods = result.get();
                GoodsStatus goodsStatus = new GoodsStatus();
                goodsStatus.setGoods(goods);
                goodsStatus.setDate(new Timestamp(new Date().getTime()));
                goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
                goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(goodsStatusDTO.getName()));
                goodsStatusDAO.insert(goodsStatus);
            } else {
                throw new ResourceNotFoundException("Goods with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void putGoodsInCells(Long goodsId, List<StorageCellDTO> storageCells) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Putting goods with id {} in cells: {}", goodsId, storageCells);
        if (goodsId == null || storageCells == null)
            throw new IllegalParametersException("Goods id or storage cell id's list is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (!result.isPresent())
                throw new ResourceNotFoundException("Goods with such id was not found");
            for (StorageCellDTO cell : storageCells) {
                StorageCell storageCell = findStorageCellById(cell.getIdStorageCell());
                if (storageCell != null) {
                    storageCell.setGoods(result.get());
                }
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    private StorageCell findStorageCellById(Long storageCellId) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for storage cell with id: {}", storageCellId);
        if (storageCellId == null) throw new IllegalParametersException("Storage cell id is null");
        Optional<StorageCell> result = storageCellDAO.findById(storageCellId);
        if (result.isPresent())
            return result.get();
        else return null;
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Removing goods with id {} from storage", goodsId);
        if (goodsId == null)
            throw new IllegalParametersException("Goods id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (result.isPresent()) {
                Goods goods = result.get();
                List<StorageCell> cells = goods.getCells();
                for (StorageCell cell : cells) {
                    cell.setGoods(null);
                }
            } else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public void setOutgoingInvoice(List<Long> goodsIds, Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Setting to goods with id's: {} outgoing invoice {} ", goodsIds, invoiceId);
        if (goodsIds == null || invoiceId == null)
            throw new IllegalParametersException("Goods id's or invoice id is null");
        try {
            Invoice invoice = findInvoiceById(invoiceId);
            if (invoice != null) {
                for (Long id : goodsIds) {
                    if (id != null) {
                        Optional<Goods> result = goodsDAO.findById(id);
                        if (result.isPresent()) {
                            Goods goods = result.get();
                            goods.setOutgoingInvoice(invoice);
                        }
                    }
                }
            } else throw new ResourceNotFoundException("Invoice with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsStatusName> getStatusNames() throws DataAccessException {
        logger.info("Getting status names list");
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        try {
            return goodsStatusNameDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during goods status names list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> getUnits() throws DataAccessException {
        logger.info("Getting units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(Unit.class);
        try {
            return unitDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during units list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageSpaceType> getStorageSpaceTypes() throws DataAccessException {
        logger.info("Getting storage space types list");
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        try {
            return storageSpaceTypeDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during storage space types list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

}
