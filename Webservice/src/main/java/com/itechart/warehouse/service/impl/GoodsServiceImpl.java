package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.dto.GoodsStatusSearchDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.query.QueryBuilder;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private StorageCellDAO storageCellDAO;
    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<Goods> findGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods starting from index {} by warehouse id: {}", maxResults, firstResult, warehouseId);
        if (warehouseId == null) throw new IllegalParametersException("Warehouse id is null");
        try {
            return goodsDAO.findByWarehouseId(warehouseId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
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
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<Goods> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods for warehouse with id {} starting from index {} by criteria: {}", maxResults, warehouseId, firstResult, goodsSearchDTO);
        if (goodsSearchDTO == null || warehouseId == null)
            throw new IllegalParametersException("Goods search DTO or warehouse id is null");

        StringBuilder root = new StringBuilder("SELECT goods FROM Goods goods");
        QueryBuilder builder = new QueryBuilder(root);
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        builder.addJoin("INNER JOIN GoodsStatus status ON status.goods = goods");
        builder.addJoin("INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice");
        builder.addJoin("INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse");

        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("warehouseId", warehouseId);


        if (goodsSearchDTO.getName() != null) {
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
            if (goodsSearchDTO.getCurrentStatus() != null) {
                builder.addRestriction("status_2.goods IS NULL AND status.goodsStatusName = :statusName");
                builder.addJoin("LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date");
                queryParameters.put("statusName", findGoodsStatusNameByName(goodsSearchDTO.getCurrentStatus()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (goodsSearchDTO.getStorageTypeName() != null) {
                builder.addRestriction("goods.storageType = :goodsStorageType");
                queryParameters.put("goodsStorageType", findStorageTypeByName(goodsSearchDTO.getStorageTypeName()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for storage type: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (goodsSearchDTO.getQuantityUnitName() != null) {
                builder.addRestriction("goods.quantityUnit = :goodsQuantityUnit");
                queryParameters.put("goodsQuantityUnit", findUnitByName(goodsSearchDTO.getQuantityUnitName()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (goodsSearchDTO.getWeightUnitName() != null) {
                builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
                queryParameters.put("goodsWeightUnit", findUnitByName(goodsSearchDTO.getWeightUnitName()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        try {
            if (goodsSearchDTO.getPriceUnitName() != null) {
                builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
                queryParameters.put("goodsPriceUnit", findUnitByName(goodsSearchDTO.getPriceUnitName()));
            }
        } catch (GenericDAOException e) {
            logger.error("Error during search for unit: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }

        if (goodsSearchDTO.getStatuses() != null) {
            int counter = 3;
            for (GoodsStatusSearchDTO statusDTO : goodsSearchDTO.getStatuses()) {
                try {
                    if (statusDTO.getStatusName() != null) {
                        builder.addRestriction("status_" + counter + ".goodsStatusName = :statusName");
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        queryParameters.put("statusName", findGoodsStatusNameByName(statusDTO.getStatusName()));
                    }
                    if (statusDTO.getFromDate() != null) {
                        builder.addRestriction("status_" + counter + ".date >= :statusFromDate_" + counter);
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        queryParameters.put("statusFromDate_" + counter, statusDTO.getFromDate());
                    }
                    if (statusDTO.getToDate() != null) {
                        builder.addRestriction("status_" + counter + ".date <= :statusToDate_" + counter);
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        queryParameters.put("statusToDate_" + counter, statusDTO.getToDate());
                    }
                    if (statusDTO.getUserFirstName() != null) {
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        builder.addJoin("INNER JOIN User user_" + counter + " ON status_" + counter + ".user = user_" + counter);
                        builder.addRestriction("user_" + counter + ".firstName <= :statusUserFirstName_" + counter);
                        queryParameters.put("statusUserFirstName_" + counter, statusDTO.getUserFirstName());
                    }
                    if (statusDTO.getUserLastName() != null) {
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        builder.addJoin("INNER JOIN User user_" + counter + " ON status_" + counter + ".user = user_" + counter);
                        builder.addRestriction("user_" + counter + ".lastName <= :statusUserLastName_" + counter);
                        queryParameters.put("statusUserLastName_" + counter, statusDTO.getUserLastName());
                    }
                    if (statusDTO.getUserPatronymic() != null) {
                        builder.addJoin("INNER JOIN GoodsStatus status_" + counter + " ON status_" + counter + ".goods = goods");
                        builder.addJoin("INNER JOIN User user_" + counter + " ON status_" + counter + ".user = user_" + counter);
                        builder.addRestriction("user_" + counter + ".patronymic <= :statusUserPatronymic_" + counter);
                        queryParameters.put("statusUserPatronymic_" + counter, statusDTO.getUserPatronymic());
                    }
                    counter++;
                } catch (GenericDAOException e) {
                    logger.error("Error during search for goods status: {}", e.getMessage());
                    throw new DataAccessException(e.getCause());
                }
            }
        }

        try {
            return goodsDAO.findByQuery(builder.build().toString(), queryParameters, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
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
    public List<GoodsStatus> findStatusesOfGoods(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find all statuses of goods with id: {}", goodsId);
        if (goodsId == null) throw new IllegalParametersException("Goods id is null");
        Optional<Goods> result = null;
        try {
            result = goodsDAO.findById(goodsId);
            if (result.isPresent())
                return result.get().getStatuses();
            else throw new ResourceNotFoundException("Goods with such id was not found");
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
                if (StringUtils.isNotBlank(goodsDTO.getStorageTypeName()))
                    goodsToUpdate.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
                else throw new IllegalParametersException("Field storage type name can not be empty");
                if (StringUtils.isNotBlank(goodsDTO.getQuantityUnitName()))
                    goodsToUpdate.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
                else throw new IllegalParametersException("Field quantity unit name can not be empty");
                if (StringUtils.isNotBlank(goodsDTO.getWeightUnitName()))
                    goodsToUpdate.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
                else throw new IllegalParametersException("Field weight unit name can not be empty");
                if (StringUtils.isNotBlank(goodsDTO.getPriceUnitName()))
                    goodsToUpdate.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
                else throw new IllegalParametersException("Field price unit name can not be empty");
                return goodsDAO.update(goodsToUpdate);
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

            if (StringUtils.isNotBlank(goodsDTO.getPriceUnitName())) {
                goods.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
            } else throw new IllegalParametersException("Field price unit name can not be empty");
            if (StringUtils.isNotBlank(goodsDTO.getQuantityUnitName())) {
                goods.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
            } else throw new IllegalParametersException("Field quantity unit name can not be empty");
            if (StringUtils.isNotBlank(goodsDTO.getWeightUnitName())) {
                goods.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
            } else throw new IllegalParametersException("Field weight unit name can not be empty");
            if (StringUtils.isNotBlank(goodsDTO.getStorageTypeName())) {
                goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
            } else throw new IllegalParametersException("Field storage type name can not be empty");
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
                goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(goodsStatusDTO.getStatusName()));
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
    public void putGoodsInCells(Long goodsId, List<Long> storageCellIds) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Putting goods with id {} in cells with ids: {}", goodsId, storageCellIds);
        if (goodsId == null || storageCellIds == null)
            throw new IllegalParametersException("Goods id or storage cell id's list is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            if (!result.isPresent())
                throw new ResourceNotFoundException("Goods with such id was not found");
            for (Long id : storageCellIds) {
                StorageCell storageCell = findStorageCellById(id);
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

}
