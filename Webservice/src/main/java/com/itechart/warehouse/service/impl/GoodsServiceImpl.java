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
import com.itechart.warehouse.service.services.InvoiceService;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private QuantityUnitDAO quantityUnitDAO;
    private WeightUnitDAO weightUnitDAO;
    private PriceUnitDAO priceUnitDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;
    private StorageCellDAO storageCellDAO;
    private WarehouseService warehouseService;
    private InvoiceService invoiceService;
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public void setQuantityUnitDAO(QuantityUnitDAO quantityUnitDAO) {
        this.quantityUnitDAO = quantityUnitDAO;
    }

    @Autowired
    public void setWeightUnitDAO(WeightUnitDAO weightUnitDAO) {
        this.weightUnitDAO = weightUnitDAO;
    }

    @Autowired
    public void setPriceUnitDAO(PriceUnitDAO priceUnitDAO) {
        this.priceUnitDAO = priceUnitDAO;
    }

    @Autowired
    @Lazy
    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Lazy
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
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
    public void setStorageSpaceTypeDAO(StorageSpaceTypeDAO storageSpaceTypeDAO) {
        this.storageSpaceTypeDAO = storageSpaceTypeDAO;
    }

    @Autowired
    public void setStorageCellDAO(StorageCellDAO storageCellDAO) {
        this.storageCellDAO = storageCellDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findAllGoods(int firstResult, int maxResults) throws DataAccessException {
        logger.info("Find {} goods starting from index {}", maxResults, firstResult);
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.addOrder(Order.desc("id"));
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
            Goods goods = goodsDAO.getById(id);
            if (goods != null)
                return goods;
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
            Goods goods = goodsDAO.getById(id);
            if (goods != null)
                return mapGoodsToDTOs(goods);
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
        logger.info("Find goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
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
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<GoodsDTO> findStoredGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find stored goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            List<Goods> goodsList = goodsDAO.findStoredGoodsByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOs(goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsDTO> findActApplicableGoods(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods applicable to act, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            List<Goods> goodsList = goodsDAO.findApplicableToActGoodsByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOs(goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public long getGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get goods count, warehouse id: {}", warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return goodsDAO.getGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for goods: {}", e);
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public long getStoredGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get stored goods count, warehouse id: {}", warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return goodsDAO.getStoredGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for goods: {}", e);
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActApplicableGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get goods applicable to act count, warehouse id: {}", warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return goodsDAO.getApplicableToActGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for goods: {}", e);
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, invoice id: {}, first result: {}, max results: {}", invoiceId, firstResult, maxResults);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
            criteria.createAlias("incomingInvoice", "invoice");
            criteria.add(Restrictions.eq("invoice.id", invoiceId));
            criteria.add(Restrictions.isNull("deleted"));
            criteria.addOrder(Order.desc("id"));
            return goodsDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e);
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsDTO> findGoodsDTOsForInvoice(Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods DTOs, invoice id: {}", invoiceId);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        return mapGoodsListToDTOs(findGoodsForInvoice(invoiceId, -1, -1));
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")// TODO: 19.05.2017
    public List<GoodsDTO> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods, warehouse id: {}, first result: {}, max results: {}, search criteria: {}", warehouseId, firstResult, maxResults, goodsSearchDTO);
        if (goodsSearchDTO == null || warehouseId == null)
            throw new IllegalParametersException("Goods search DTO or warehouse id is null");

        StringBuilder root = new StringBuilder("SELECT DISTINCT goods FROM Goods goods");
        QueryBuilder builder = new QueryBuilder(root);
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        builder.addJoin("INNER JOIN GoodsStatus status ON status = goods.currentStatus");
        builder.addJoin("INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse");


        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("warehouseId", warehouseId);

        if (goodsSearchDTO.getActApplicable() != null) {
            if (goodsSearchDTO.getActApplicable()) {
                builder.addJoin("INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName");
                if (StringUtils.isNotBlank(goodsSearchDTO.getActType())) {
                    switch (ActTypeEnum.valueOf(goodsSearchDTO.getActType())) {
                        case ACT_OF_LOSS:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case ACT_OF_THEFT:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case WRITE_OFF_ACT:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case MISMATCH_ACT:
                            builder.addRestriction("statusName.name = 'REGISTERED'");
                            break;
                        default:
                            break;
                    }
                } else {
                    builder.addRestriction("statusName.name <> 'MOVED_OUT'");
                    builder.addRestriction("statusName.name <> 'STOLEN'");
                    builder.addRestriction("statusName.name <> 'CHECKED'");
                    builder.addRestriction("statusName.name <> 'RELEASE_ALLOWED'");
                    builder.addRestriction("statusName.name <> 'SEIZED'");
                    builder.addRestriction("statusName.name <> 'TRANSPORT_COMPANY_MISMATCH'");
                    builder.addRestriction("statusName.name <> 'RECYCLED'");
                    builder.addRestriction("statusName.name <> 'LOST_BY_WAREHOUSE_COMPANY'");
                    builder.addRestriction("statusName.name <> 'LOST_BY_TRANSPORT_COMPANY'");
                    builder.addRestriction("statusName.name IS NOT NULL");
                }

            }
        }

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
        if (goodsSearchDTO.getIncomingInvoiceId() != null) {
            builder.addJoin("INNER JOIN Invoice incomingInvoice ON goods.incomingInvoice = incomingInvoice");
            builder.addRestriction("incomingInvoice.id = :incomingInvoiceId");
            queryParameters.put("incomingInvoiceId", goodsSearchDTO.getIncomingInvoiceId());
        }

        if (goodsSearchDTO.getOutgoingInvoiceId() != null) {
            builder.addRestriction("outgoingInvoice.id = :outgoingInvoice");
            builder.addJoin("INNER JOIN Invoice outgoingInvoice ON goods.outgoingInvoice = outgoingInvoice");
            queryParameters.put("outgoingInvoice", goodsSearchDTO.getOutgoingInvoiceId());
        }


        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getCurrentStatus())) {
                builder.addRestriction("status.goodsStatusName = :statusName");
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
                queryParameters.put("goodsQuantityUnit", findQuantityUnitByName(goodsSearchDTO.getQuantityUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getWeightUnit())) {
                builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
                queryParameters.put("goodsWeightUnit", findWeightUnitByName(goodsSearchDTO.getWeightUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getPriceUnit())) {
                builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
                queryParameters.put("goodsPriceUnit", findPriceUnitByName(goodsSearchDTO.getPriceUnit()));
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
        builder.addRestriction("goods.deleted IS NULL");
        builder.addOrderBy("ORDER BY goods.id DESC");

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
        logger.info("Get goods count, warehouse id: {}, search criteria: {}", warehouseId, goodsSearchDTO);
        if (warehouseId == null || goodsSearchDTO == null)
            throw new IllegalParametersException("Warehouse id or DTO is null");
        StringBuilder root = new StringBuilder("SELECT count(DISTINCT goods.id) FROM Goods goods");
        QueryBuilder builder = new QueryBuilder(root);
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        builder.addJoin("INNER JOIN GoodsStatus status ON status = goods.currentStatus");
        builder.addJoin("INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse");

        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("warehouseId", warehouseId);

        if (goodsSearchDTO.getActApplicable() != null) {
            if (goodsSearchDTO.getActApplicable()) {
                builder.addJoin("INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName");
                if (StringUtils.isNotBlank(goodsSearchDTO.getActType())) {
                    switch (ActTypeEnum.valueOf(goodsSearchDTO.getActType())) {
                        case ACT_OF_LOSS:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case ACT_OF_THEFT:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case WRITE_OFF_ACT:
                            builder.addRestriction("(statusName.name = 'STORED' OR statusName.name = 'WITHDRAWN')");
                            break;
                        case MISMATCH_ACT:
                            builder.addRestriction("statusName.name = 'REGISTERED'");
                            break;
                        default:
                            break;
                    }
                } else {
                    builder.addRestriction("statusName.name <> 'MOVED_OUT'");
                    builder.addRestriction("statusName.name <> 'STOLEN'");
                    builder.addRestriction("statusName.name <> 'CHECKED'");
                    builder.addRestriction("statusName.name <> 'RELEASE_ALLOWED'");
                    builder.addRestriction("statusName.name <> 'SEIZED'");
                    builder.addRestriction("statusName.name <> 'TRANSPORT_COMPANY_MISMATCH'");
                    builder.addRestriction("statusName.name <> 'RECYCLED'");
                    builder.addRestriction("statusName.name <> 'LOST_BY_WAREHOUSE_COMPANY'");
                    builder.addRestriction("statusName.name <> 'LOST_BY_TRANSPORT_COMPANY'");
                    builder.addRestriction("statusName.name IS NOT NULL");
                }


            }
        }

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

        if (goodsSearchDTO.getIncomingInvoiceId() != null) {
            builder.addJoin("INNER JOIN Invoice incomingInvoice ON goods.incomingInvoice = incomingInvoice");
            builder.addRestriction("incomingInvoice.id = :incomingInvoiceId");
            queryParameters.put("incomingInvoiceId", goodsSearchDTO.getIncomingInvoiceId());
        }

        if (goodsSearchDTO.getOutgoingInvoiceId() != null) {
            builder.addJoin("INNER JOIN Invoice outgoingInvoice ON goods.outgoingInvoice = outgoingInvoice");
            builder.addRestriction("outgoingInvoice.id = :outgoingInvoice");
            queryParameters.put("outgoingInvoice", goodsSearchDTO.getOutgoingInvoiceId());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getCurrentStatus())) {
                builder.addRestriction("status.goodsStatusName = :statusName");
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
                queryParameters.put("goodsQuantityUnit", findQuantityUnitByName(goodsSearchDTO.getQuantityUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getWeightUnit())) {
                builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
                queryParameters.put("goodsWeightUnit", findWeightUnitByName(goodsSearchDTO.getWeightUnit()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (StringUtils.isNotBlank(goodsSearchDTO.getPriceUnit())) {
                builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
                queryParameters.put("goodsPriceUnit", findPriceUnitByName(goodsSearchDTO.getPriceUnit()));
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
        builder.addRestriction("goods.deleted IS NULL");

        try {
            return goodsDAO.getCountByQuery(builder.build().toString(), queryParameters);
        } catch (GenericDAOException e) {
            logger.error("Error during getting count: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
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
    public Warehouse findWarehouseOwnedBy(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Goods goods = findGoodsById(goodsId);
        return goods.getWarehouse();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyOwnedBy(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Goods goods = findGoodsById(goodsId);
        return goods.getWarehouse().getWarehouseCompany();
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
                if (!isActApplicable(goodsToUpdate)) return null;
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
                        goodsToUpdate.setPriceUnit(findPriceUnitByName(goodsDTO.getPriceUnit().getName()));
                } else throw new IllegalParametersException("Price unit can not be empty");
                if (goodsDTO.getQuantityUnit() != null) {
                    if (goodsDTO.getQuantityUnit().getName() != null)
                        goodsToUpdate.setQuantityUnit(findQuantityUnitByName(goodsDTO.getQuantityUnit().getName()));
                } else throw new IllegalParametersException("Quantity unit can not be empty");
                if (goodsDTO.getWeightUnit() != null) {
                    if (goodsDTO.getWeightUnit().getName() != null)
                        goodsToUpdate.setWeightUnit(findWeightUnitByName(goodsDTO.getWeightUnit().getName()));
                } else throw new IllegalParametersException("Weight unit can not be empty");
                if (goodsDTO.getStorageType() != null) {
                    if (goodsDTO.getStorageType().getName() != null)
                        goodsToUpdate.setStorageType(findStorageTypeByName(goodsDTO.getStorageType().getName()));
                } else throw new IllegalParametersException("Storage type can not be empty");
                Goods savedGoods = goodsDAO.update(goodsToUpdate);

                if (goodsDTO.getCurrentStatus() != null) {
                    if (StringUtils.isNotBlank(goodsDTO.getCurrentStatus().getName())) {
                        GoodsStatusDTO goodsStatusDTO = new GoodsStatusDTO();
                        goodsStatusDTO.setName(goodsDTO.getCurrentStatus().getName());
                        GoodsStatus goodsStatus = setGoodsStatus(savedGoods.getId(), goodsStatusDTO);
                        if (goodsStatus != null) {
                            savedGoods.setCurrentStatus(goodsStatus);
                            if (goodsStatus.getGoodsStatusName().getName().equals(GoodsStatusEnum.REGISTERED.toString())) {
                                savedGoods.setRegisteredStatus(goodsStatus);
                            }
                            if (goodsStatus.getGoodsStatusName().getName().equals(GoodsStatusEnum.MOVED_OUT.toString())) {
                                savedGoods.setRegisteredStatus(goodsStatus);
                            }
                        }
                    }
                }
                return savedGoods;

            } else throw new ResourceNotFoundException("Goods with such id was not found");
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
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
            if (goodsDTO.getWarehouseId() != null)
                goods.setWarehouse(warehouseService.findWarehouseById(goodsDTO.getWarehouseId()));
            else throw new IllegalParametersException("Warehouse id can not be null");

            if (goodsDTO.getPriceUnit() != null) {
                if (goodsDTO.getPriceUnit().getName() != null)
                    goods.setPriceUnit(findPriceUnitByName(goodsDTO.getPriceUnit().getName()));
            } else throw new IllegalParametersException("Price unit can not be empty");
            if (goodsDTO.getQuantityUnit() != null) {
                if (goodsDTO.getQuantityUnit().getName() != null)
                    goods.setQuantityUnit(findQuantityUnitByName(goodsDTO.getQuantityUnit().getName()));
            } else throw new IllegalParametersException("Quantity unit can not be empty");
            if (goodsDTO.getWeightUnit() != null) {
                if (goodsDTO.getWeightUnit().getName() != null)
                    goods.setWeightUnit(findWeightUnitByName(goodsDTO.getWeightUnit().getName()));
            } else throw new IllegalParametersException("Weight unit can not be empty");
            if (goodsDTO.getStorageType() != null) {
                if (goodsDTO.getStorageType().getName() != null)
                    goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageType().getName()));
            } else throw new IllegalParametersException("Storage type can not be empty");
            Invoice invoice = invoiceService.findInvoiceById(invoiceId);
            if (invoice != null)
                goods.setIncomingInvoice(invoice);
            else throw new ResourceNotFoundException("Invoice with such id was not found");
            Goods savedGoods = goodsDAO.insert(goods);
            if (savedGoods != null) {
                GoodsStatusDTO goodsStatus = new GoodsStatusDTO();
                goodsStatus.setName(GoodsStatusEnum.REGISTERED.toString());
                setGoodsStatus(savedGoods.getId(), goodsStatus);
            }
            return savedGoods;
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    /**
     * Splits goods entry into two: one affected by act, other one - not affected.
     * Quantity, weight and price split between two entries.
     *
     * @param actType   type of act, used to set status of affected by act goods.
     * @param goodsList list of goods affected by act and have to be split.
     * @return list of newly created goods entries, affected by act.
     */
    @Override
    public List<GoodsDTO> splitGoodsForAct(String actType, List<GoodsDTO> goodsList) throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Splitting goods to become part of new act with type {} for list of goods: {}", actType, goodsList);
        if (actType == null || goodsList == null)
            throw new IllegalParametersException("Act type or goods list is null");
        List<GoodsDTO> returnedList = new ArrayList<>();
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


            for (GoodsDTO goods : goodsList) {
                if (goods != null) {
                    Optional<Goods> goodsResult = goodsDAO.findById(goods.getId());
                    if (goodsResult.isPresent()) {
                        Goods initialGoods = goodsResult.get();
                        if (goods.getQuantity().compareTo(initialGoods.getQuantity()) == 0) {
                            //if all amount is affected by act
                            returnedList.add(GoodsDTO.buildGoodsDTO(initialGoods));
                            GoodsStatusDTO goodsStatusDTO = new GoodsStatusDTO();
                            goodsStatusDTO.setName(statusName);
                            setGoodsStatus(initialGoods.getId(), goodsStatusDTO);
                            removeGoodsFromStorage(initialGoods.getId());

                        } else {
                            Goods goodsInAct = new Goods(initialGoods);
                            goodsInAct.setStatuses(new ArrayList<>());//emptying statuses for affected by act goods
                            goodsInAct.setCells(new ArrayList<>());//emptying cells for affected by act goods
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
                                GoodsStatusDTO goodsStatus = new GoodsStatusDTO();
                                goodsStatus.setName(statusName);
                                setGoodsStatus(returnedGoods.getId(), goodsStatus);

                            }
                            returnedList.add(GoodsDTO.buildGoodsDTO(returnedGoods));
                        }
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

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Goods', 'DELETE')")
    public void deleteGoods(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Deleting goods with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Goods goods = goodsDAO.getById(id);
            if (goods != null) {
                goods.setDeleted(new java.sql.Date(DateTime.now().toDate().getTime()));
                removeGoodsFromStorage(id);
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
    public GoodsStatus setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Setting status: {} to goods with id {}", goodsStatusDTO, goodsId);
        if (goodsId == null || goodsStatusDTO == null)
            throw new IllegalParametersException("Goods status DTO or goods id is null");
        try {
            Goods goods = goodsDAO.getById(goodsId);
            if (goods != null) {
                if (!validateNewStatus(goods, goodsStatusDTO.getName())) {
                    return null;
                }
                GoodsStatus goodsStatus = new GoodsStatus();
                goodsStatus.setGoods(goods);
                goodsStatus.setDate(new Timestamp(new Date().getTime()));
                goodsStatus.setUser(userService.findUserById(UserDetailsProvider.getUserDetails().getUserId()));
                goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(goodsStatusDTO.getName()));
                goodsStatusDAO.insert(goodsStatus);
                goods.setCurrentStatus(goodsStatus);
                if (goodsStatus.getGoodsStatusName().getName().equals(GoodsStatusEnum.REGISTERED.toString())) {
                    goods.setRegisteredStatus(goodsStatus);
                }
                if (goodsStatus.getGoodsStatusName().getName().equals(GoodsStatusEnum.MOVED_OUT.toString())) {
                    goods.setMovedOutStatus(goodsStatus);
                }
                if (!goodsStatus.getGoodsStatusName().getName().equals(GoodsStatusEnum.STORED.toString())) {
                    removeGoodsFromStorage(goods.getId());
                }
                return goodsStatus;
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
            Goods goods = goodsDAO.getById(goodsId);
            //if status is not one of listed then goods cant be put in storage cells
            if (!hasAnyStatus(goods, GoodsStatusEnum.CHECKED, GoodsStatusEnum.STORED, GoodsStatusEnum.WITHDRAWN, GoodsStatusEnum.RELEASE_ALLOWED)) {
                return;
            }
            if (goods == null)
                throw new ResourceNotFoundException("Goods with such id was not found");
            for (StorageCellDTO cell : storageCells) {
                StorageCell storageCell = findStorageCellById(cell.getIdStorageCell());
                if (storageCell != null) {
                    storageCell.setGoods(goods);
                }
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Removing goods with id {} from storage", goodsId);
        if (goodsId == null)
            throw new IllegalParametersException("Goods id is null");

        try {
            Goods goods = goodsDAO.getById(goodsId);
            //if status is not stored then can not be removed from storage
            if (!hasAnyStatus(goods, GoodsStatusEnum.STORED)) {
                return;
            }
            if (goods != null) {
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
            Invoice invoice = invoiceService.findInvoiceById(invoiceId);
            if (invoice != null) {
                for (Long id : goodsIds) {
                    if (id != null) {
                        Goods goods = goodsDAO.getById(id);
                        if (goods != null) {
                            //if one of listed statuses set then cant be a part of outgoing invoice
                            if (!hasAnyStatus(goods,
                                    GoodsStatusEnum.MOVED_OUT,
                                    GoodsStatusEnum.STOLEN,
                                    GoodsStatusEnum.SEIZED,
                                    GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH,
                                    GoodsStatusEnum.RECYCLED,
                                    GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY,
                                    GoodsStatusEnum.LOST_BY_WAREHOUSE_COMPANY)) {
                                goods.setOutgoingInvoice(invoice);
                            }
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
    public List<QuantityUnit> getQuantityUnits() throws DataAccessException {
        logger.info("Getting units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(QuantityUnit.class);
        try {
            return quantityUnitDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during units list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<WeightUnit> getWeightUnits() throws DataAccessException {
        logger.info("Getting units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(WeightUnit.class);
        try {
            return weightUnitDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during units list retrieval: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<PriceUnit> getPriceUnits() throws DataAccessException {
        logger.info("Getting units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceUnit.class);
        try {
            return priceUnitDAO.findAll(criteria, -1, -1);
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

    @Override
    @Transactional
    public Goods saveGoodsForOutgoingInvoice(Goods goodsForInvoice) throws GenericDAOException {
        Goods savedGoods = null;
        if (goodsForInvoice != null) {
            savedGoods = goodsDAO.insert(goodsForInvoice);
        }

        return savedGoods;
    }

    @Override
    public GoodsDTO mapToDto(Goods goods) {
        GoodsDTO dto = new GoodsDTO();
        dto.setId(goods.getId());
        dto.setName(goods.getName());
        dto.setQuantity(goods.getQuantity());
        dto.setWeight(goods.getWeight());
        dto.setPrice(goods.getPrice());
        dto.setStorageType(goods.getStorageType());
        dto.setQuantityUnit(goods.getQuantityUnit());
        dto.setWeightUnit(goods.getWeightUnit());
        dto.setPriceUnit(goods.getPriceUnit());

        return dto;
    }

    @Override
    public boolean isActApplicable(List<GoodsDTO> goodsDTOList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        for (GoodsDTO goodsDTO : goodsDTOList) {
            Goods goods = findGoodsById(goodsDTO.getId());
            if (!isActApplicable(goods)) {
                return false;
            }
        }
        return true;
    }

    private boolean isActApplicable(Goods goods) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        if (hasAnyStatus(goods,
                GoodsStatusEnum.MOVED_OUT,
                GoodsStatusEnum.STOLEN,
                GoodsStatusEnum.SEIZED,
                GoodsStatusEnum.CHECKED,
                GoodsStatusEnum.RELEASE_ALLOWED,
                GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH,
                GoodsStatusEnum.RECYCLED,
                GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY,
                GoodsStatusEnum.LOST_BY_WAREHOUSE_COMPANY)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasStatus(Goods goods, GoodsStatusEnum status) {
        if (goods == null) throw new IllegalArgumentException("Goods is null");
        if (status == null) throw new IllegalArgumentException("Status name is null");
        GoodsStatus currentStatus = goods.getCurrentStatus();
        if (goods.getCurrentStatus() == null) return false;
        if (status.toString().equals(currentStatus.getGoodsStatusName().getName()))
            return true;
        return false;

    }

    private boolean hasAnyStatus(Goods goods, GoodsStatusEnum... statusNames) {
        if (goods == null) throw new IllegalArgumentException("Goods is null");
        for (GoodsStatusEnum status : statusNames) {
            if (status != null) {
                if (hasStatus(goods, status)) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean hasAnyStatus(Goods goods) {
        if (goods == null) throw new IllegalArgumentException("Goods is null");
        return hasAnyStatus(goods, GoodsStatusEnum.REGISTERED,
                GoodsStatusEnum.CHECKED,
                GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY,
                GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH,
                GoodsStatusEnum.STORED,
                GoodsStatusEnum.STOLEN,
                GoodsStatusEnum.LOST_BY_WAREHOUSE_COMPANY,
                GoodsStatusEnum.RECYCLED,
                GoodsStatusEnum.SEIZED,
                GoodsStatusEnum.WITHDRAWN,
                GoodsStatusEnum.RELEASE_ALLOWED,
                GoodsStatusEnum.MOVED_OUT);

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

    private StorageCell findStorageCellById(Long storageCellId) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for storage cell with id: {}", storageCellId);
        if (storageCellId == null) throw new IllegalParametersException("Storage cell id is null");
        Optional<StorageCell> result = storageCellDAO.findById(storageCellId);
        if (result.isPresent())
            return result.get();
        else return null;
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

    private QuantityUnit findQuantityUnitByName(String unitName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for unit with name: {}", unitName);
        if (unitName == null) throw new IllegalParametersException("Unit name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(QuantityUnit.class);
        criteria.add(Restrictions.eq("name", unitName));
        List<QuantityUnit> fetchedUnits = quantityUnitDAO.findAll(criteria, -1, 1);
        if (!fetchedUnits.isEmpty())
            return fetchedUnits.get(0);
        else throw new IllegalParametersException("Invalid quantity unit name: " + unitName);
    }
    private WeightUnit findWeightUnitByName(String unitName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for unit with name: {}", unitName);
        if (unitName == null) throw new IllegalParametersException("Unit name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(WeightUnit.class);
        criteria.add(Restrictions.eq("name", unitName));
        List<WeightUnit> fetchedUnits = weightUnitDAO.findAll(criteria, -1, 1);
        if (!fetchedUnits.isEmpty())
            return fetchedUnits.get(0);
        else throw new IllegalParametersException("Invalid weight unit name: " + unitName);
    }

    private PriceUnit findPriceUnitByName(String unitName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for unit with name: {}", unitName);
        if (unitName == null) throw new IllegalParametersException("Unit name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceUnit.class);
        criteria.add(Restrictions.eq("name", unitName));
        List<PriceUnit> fetchedUnits = priceUnitDAO.findAll(criteria, -1, 1);
        if (!fetchedUnits.isEmpty())
            return fetchedUnits.get(0);
        else throw new IllegalParametersException("Invalid price unit name: " + unitName);
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

    private boolean validateNewStatus(Goods goods, String newStatus) {
        if (goods == null) {
            throw new IllegalArgumentException("Goods is null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status name is null");
        }
        if (goods.getCurrentStatus() == null) {
            return true;
        }
        GoodsStatusEnum val = GoodsStatusEnum.valueOf(goods.getCurrentStatus().getGoodsStatusName().getName());
        switch (val) {
            case REGISTERED:
                return newStatus.equals(GoodsStatusEnum.CHECKED.toString()) ||
                        newStatus.equals(GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH.toString()) ||
                        newStatus.equals(GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY.toString());
            case CHECKED:
                return newStatus.equals(GoodsStatusEnum.STORED.toString());
            case STORED:
                return !newStatus.equals(GoodsStatusEnum.CHECKED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.REGISTERED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.MOVED_OUT.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.RELEASE_ALLOWED.toString());
            case STOLEN:
                return false;
            case SEIZED:
                return false;
            case TRANSPORT_COMPANY_MISMATCH:
                return false;
            case LOST_BY_TRANSPORT_COMPANY:
                return false;
            case LOST_BY_WAREHOUSE_COMPANY:
                return false;
            case RECYCLED:
                return false;
            case WITHDRAWN:
                return !newStatus.equals(GoodsStatusEnum.CHECKED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.REGISTERED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.MOVED_OUT.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY.toString());
            case RELEASE_ALLOWED:
                return newStatus.equals(GoodsStatusEnum.STORED.toString()) ||
                        newStatus.equals(GoodsStatusEnum.MOVED_OUT.toString());
            case MOVED_OUT:
                return false;
            default:
                return false;
        }

    }

}
