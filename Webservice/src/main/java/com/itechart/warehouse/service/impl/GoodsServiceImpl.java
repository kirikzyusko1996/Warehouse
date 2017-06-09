package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.*;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.query.GoodsSearchCriteria;
import com.itechart.warehouse.query.GoodsStatusSearchCriteria;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import com.itechart.warehouse.service.services.InvoiceService;
import com.itechart.warehouse.service.services.UserService;
import com.itechart.warehouse.service.services.WarehouseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of goods service.
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private static final String ERROR_ID_IS_NULL = "Id is null";
    private static final String ERROR_INVOICE_ID_IS_NULL = "Invoice id is null";
    private static final String ERROR_WAREHOUSE_ID_IS_NULL = "Warehouse id is null";
    private static final String ERROR_GOODS_IS_NULL = "Goods is null";
    private static final String ERROR_GOODS_ID_IS_NULL = "Goods id is null";
    private static final String ERROR_GOODS_DTO_IS_NULL = "Goods DTO is null";

    private Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

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
    public Goods findGoodsById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            Goods goods = goodsDAO.getById(id);
            if (goods != null) {
                return goods;
            } else {
                throw new ResourceNotFoundException("Goods with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsDTO findGoodsDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }

        try {
            Goods goods = goodsDAO.getById(id);
            if (goods != null) {
                return mapGoodsToDTO(goods);
            } else {
                throw new ResourceNotFoundException("Goods with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<GoodsDTO> findGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            List<Goods> goodsList = goodsDAO.findByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOList(goodsList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<GoodsDTO> findStoredGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find stored goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            List<Goods> goodsList = goodsDAO.findStoredGoodsByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOList(goodsList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsDTO> findActApplicableGoods(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods applicable to act, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            List<Goods> goodsList = goodsDAO.findApplicableToActGoodsByWarehouseId(warehouseId, firstResult, maxResults);
            return mapGoodsListToDTOList(goodsList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public long getGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get goods count, warehouse id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            return goodsDAO.getGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public long getStoredGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get stored goods count, warehouse id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            return goodsDAO.getStoredGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActApplicableGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException {
        logger.info("Get goods applicable to act count, warehouse id: {}", warehouseId);
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            return goodsDAO.getApplicableToActGoodsCount(warehouseId);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findGoodsForIncomingInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, invoice id: {}, first result: {}, max results: {}", invoiceId, firstResult, maxResults);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }

        try {
            return goodsDAO.findGoodsForIncomingInvoice(invoiceId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findGoodsForOutgoingInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, invoice id: {}, first result: {}, max results: {}", invoiceId, firstResult, maxResults);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }

        try {
            return goodsDAO.findGoodsForOutgoingInvoice(invoiceId, firstResult, maxResults);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsDTO> findGoodsDTOsForInvoice(Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find goods, invoice id: {}", invoiceId);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }

        List<Goods> goods = findGoodsForIncomingInvoice(invoiceId, -1, -1);
        return mapGoodsListToDTOList(goods);
    }

    @Override
    @Transactional(readOnly = true)
//    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")// TODO: 19.05.2017
    public List<GoodsDTO> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods, warehouse id: {}, first result: {}, max results: {}, search criteria: {}", warehouseId, firstResult, maxResults, goodsSearchDTO);
        if (goodsSearchDTO == null) {
            throw new IllegalParametersException("Goods search DTO is null");
        }
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            GoodsSearchCriteria criteria = convertGoodsSearchDTOToCriteria(goodsSearchDTO);
            List<Goods> goodsList = goodsDAO.findGoodsForWarehouseByCriteria(warehouseId, criteria, firstResult, maxResults);
            return mapGoodsListToDTOList(goodsList);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getGoodsSearchResultCount(Long warehouseId, GoodsSearchDTO goodsSearchDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Get goods count, warehouse id: {}, search criteria: {}", warehouseId, goodsSearchDTO);
        if (goodsSearchDTO == null) {
            throw new IllegalParametersException("Goods search DTO is null");
        }
        if (warehouseId == null) {
            throw new IllegalParametersException(ERROR_WAREHOUSE_ID_IS_NULL);
        }

        try {
            GoodsSearchCriteria criteria = convertGoodsSearchDTOToCriteria(goodsSearchDTO);
            return goodsDAO.getGoodsSearchResultCount(warehouseId, criteria);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsStatusDTO> findStatusesOfGoods(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find statuses, goods id: {}", goodsId);
        if (goodsId == null) {
            throw new IllegalParametersException(ERROR_GOODS_ID_IS_NULL);
        }

        try {
            List<GoodsStatus> statuses = goodsStatusDAO.findByGoodsId(goodsId);
            return mapGoodsStatusListToDTOList(statuses);
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseOwnedBy(Long goodsId) throws
            IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find warehouse, goods id: {}", goodsId);

        if (goodsId == null) {
            throw new IllegalParametersException(ERROR_GOODS_ID_IS_NULL);
        }

        Goods goods = findGoodsById(goodsId);
        return goods.getWarehouse();
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Goods', 'UPDATE')")
    public Goods updateGoods(Long id, GoodsDTO goodsDTO) throws
            DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update goods, id {}, DTO: {}", id, goodsDTO);
        if (id == null) {
            throw new IllegalParametersException(ERROR_ID_IS_NULL);
        }
        if (goodsDTO == null) {
            throw new IllegalParametersException(ERROR_GOODS_DTO_IS_NULL);
        }

        try {
            Goods goodsToUpdate = findGoodsById(id);

            if (!isUpdatable(goodsToUpdate)) {
                return null;
            }

            updateRequiredFieldsFromDTO(goodsToUpdate, goodsDTO);
            Goods savedGoods = goodsDAO.update(goodsToUpdate);

            if (goodsDTO.getCurrentStatus() != null && StringUtils.isNotBlank(goodsDTO.getCurrentStatus().getName())) {
                setGoodsStatus(savedGoods.getId(), GoodsStatusEnum.valueOf(goodsDTO.getCurrentStatus().getName()));
            }

            return savedGoods;
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#invoiceId, 'Invoice', 'GET')")
    public Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create goods, invoice id {},  DTO: {}", invoiceId, goodsDTO);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }
        if (goodsDTO == null) {
            throw new IllegalParametersException(ERROR_GOODS_DTO_IS_NULL);
        }

        try {
            Goods goods = new Goods();

            updateRequiredFieldsFromDTO(goods, goodsDTO);
            updateWarehouseField(goods, goodsDTO);
            updateInvoiceField(goods, invoiceId);

            Goods savedGoods = goodsDAO.insert(goods);
            setGoodsStatus(savedGoods.getId(), GoodsStatusEnum.REGISTERED);

            return savedGoods;

        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    /**
     * Splits goods entry into two: one affected by act, other one - not affected.
     * Quantity, weight and price split between two entries.
     *
     * @param actType   type of act, used to set status of affected by act goods.
     * @param goodsList list of goods affected by act and have to be split.
     * @return list of goods entries, affected by act.
     */
    @Override
    public List<Goods> updateAndGetGoodsForAct(String actType, List<GoodsDTO> goodsList) throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Update and get goods for act, act type {}, list of goods: {}", actType, goodsList);
        if (actType == null) {
            throw new IllegalParametersException("Act type is null");
        }
        if (goodsList == null) {
            throw new IllegalParametersException(ERROR_GOODS_IS_NULL);
        }

        String statusName = getGoodsStatusNameForAct(actType);
        List<Goods> goodsInActList = new ArrayList<>();
        try {
            for (GoodsDTO goodsDTO : goodsList) {
                if (goodsDTO != null) {
                    Goods initialGoods = findGoodsById(goodsDTO.getId());

                    if (goodsDTO.getQuantity().compareTo(initialGoods.getQuantity()) == 0) {
                        //if all amount is affected by act
                        goodsInActList.add(initialGoods);
                        setGoodsStatus(initialGoods.getId(), GoodsStatusEnum.valueOf(statusName));
                        removeGoodsFromStorage(initialGoods.getId());
                    } else {
                        Goods goodsInAct = createGoodsForAct(initialGoods, goodsDTO);
                        if (goodsInAct != null) {
                            setGoodsStatus(goodsInAct.getId(), GoodsStatusEnum.valueOf(statusName));
                            goodsInActList.add(goodsInAct);
                        }
                    }
                }
            }
            return goodsInActList;
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private Goods createGoodsForAct(Goods initialGoods, GoodsDTO goodsInActDTO) throws GenericDAOException, IllegalParametersException {
        Assert.notNull(goodsInActDTO, ERROR_GOODS_DTO_IS_NULL);

        Goods goodsInAct = new Goods(initialGoods);
        goodsInAct.setStatuses(new ArrayList<>());//emptying statuses for affected by act goods
        goodsInAct.setCells(new ArrayList<>());//emptying cells for affected by act goods
        initialGoods.setActs(new ArrayList<>());//emptying acts for not affected by act goods

        if (goodsInActDTO.getQuantity().compareTo(initialGoods.getQuantity()) <= 0) {
            goodsInAct.setQuantity(goodsInActDTO.getQuantity());
            initialGoods.setQuantity(initialGoods.getQuantity().subtract(goodsInActDTO.getQuantity()));
        } else {
            throw new IllegalParametersException("Quantity covered by act can not be greater than initial value");
        }

        if (goodsInActDTO.getWeight().compareTo(initialGoods.getWeight()) <= 0) {
            goodsInAct.setWeight(goodsInActDTO.getWeight());
            initialGoods.setWeight(initialGoods.getWeight().subtract(goodsInActDTO.getWeight()));
        } else {
            throw new IllegalParametersException("Weight covered by act can not be greater than initial value");
        }

        if (goodsInActDTO.getPrice().compareTo(initialGoods.getPrice()) <= 0) {
            goodsInAct.setPrice(goodsInActDTO.getPrice());
            initialGoods.setPrice(initialGoods.getPrice().subtract(goodsInActDTO.getPrice()));
        } else {
            throw new IllegalParametersException("Price covered by act can not be greater than initial value");
        }

        return goodsDAO.insert(goodsInAct);
    }

    private Goods createGoodsForInvoice(Goods initialGoods, GoodsDTO goodsInInvoiceDTO) throws GenericDAOException, IllegalParametersException {
        Assert.notNull(goodsInInvoiceDTO, ERROR_GOODS_DTO_IS_NULL);

        Goods goodsInInvoice = new Goods(initialGoods);
        goodsInInvoice.setStatuses(new ArrayList<>());//emptying statuses for affected by act goods
        goodsInInvoice.setCells(new ArrayList<>());//emptying cells for affected by act goods
        initialGoods.setActs(new ArrayList<>());//emptying acts for not affected by act goods

        if (goodsInInvoiceDTO.getQuantity().compareTo(initialGoods.getQuantity()) <= 0) {
            goodsInInvoice.setQuantity(goodsInInvoiceDTO.getQuantity());
            initialGoods.setQuantity(initialGoods.getQuantity().subtract(goodsInInvoiceDTO.getQuantity()));
        } else {
            throw new IllegalParametersException("Quantity in invoice can not be greater than initial value");
        }

        if (goodsInInvoiceDTO.getWeight().compareTo(initialGoods.getWeight()) <= 0) {
            goodsInInvoice.setWeight(goodsInInvoiceDTO.getWeight());
            initialGoods.setWeight(initialGoods.getWeight().subtract(goodsInInvoiceDTO.getWeight()));
        } else {
            throw new IllegalParametersException("Weight in invoice can not be greater than initial value");
        }

        if (goodsInInvoiceDTO.getPrice().compareTo(initialGoods.getPrice()) <= 0) {
            goodsInInvoice.setPrice(goodsInInvoiceDTO.getPrice());
            initialGoods.setPrice(initialGoods.getPrice().subtract(goodsInInvoiceDTO.getPrice()));
        } else {
            throw new IllegalParametersException("Price in invoice can not be greater than initial value");
        }

        return goodsDAO.insert(goodsInInvoice);
    }


    private String getGoodsStatusNameForAct(String actTypeName) {
        Assert.notNull(actTypeName, "Act type name is null");
        String statusName = null;
        switch (ActTypeEnum.valueOf(actTypeName)) {
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
            default:
                break;
        }
        return statusName;
    }

    @Override
    @Transactional
    public List<Goods> createGoodsBatch(Long invoiceId, List<GoodsDTO> goodsDTOList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Create goods batch, DTO list: {}, invoice id: {}", goodsDTOList, invoiceId);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }
        if (goodsDTOList == null) {
            throw new IllegalParametersException(ERROR_GOODS_IS_NULL);
        }

        List<Goods> goodsList = new ArrayList<>();
        for (GoodsDTO dto : goodsDTOList) {
            goodsList.add(createGoods(invoiceId, dto));
        }
        return goodsList;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Goods', 'DELETE')")
    public void deleteGoods(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete goods, id: {}", id);
        if (id == null) {
            throw new IllegalParametersException("Id is null");
        }

        try {
            Goods goods = goodsDAO.getById(id);
            if (goods != null) {
                goods.setDeleted(new java.sql.Date(DateTime.now().toDate().getTime()));
                removeGoodsFromStorage(id);
            } else {
                throw new ResourceNotFoundException("Goods with id " + id + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public GoodsStatus setGoodsStatus(Long goodsId, GoodsStatusEnum status) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Set status: {}, goods id {}", status, goodsId);
        if (goodsId == null) {
            throw new IllegalParametersException(ERROR_GOODS_ID_IS_NULL);
        }
        if (status == null) {
            throw new IllegalParametersException("Goods status is null");
        }

        try {
            Goods goods = goodsDAO.getById(goodsId);
            if (goods != null) {
                if (!validateNewStatus(goods, status.toString())) {
                    return null;
                }

                GoodsStatus goodsStatus = buildGoodsStatus(status.toString());
                goodsStatus.setGoods(goods);
                goodsStatusDAO.insert(goodsStatus);
                updateGoodsAfterStatusUpdated(goods, goodsStatus);

                return goodsStatus;
            } else {
                throw new ResourceNotFoundException("Goods with id " + goodsId + " was not found");
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private void updateGoodsAfterStatusUpdated(Goods goods, GoodsStatus goodsStatus) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        Assert.notNull(goods, ERROR_GOODS_IS_NULL);
        Assert.notNull(goods, "Goods status is null");

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
    }

    private GoodsStatus buildGoodsStatus(String statusName) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        Assert.notNull(statusName, "Status name is null");

        User user = getAuthenticatedUser();
        if (user != null) {
            GoodsStatus goodsStatus = new GoodsStatus();
            goodsStatus.setUser(user);
            goodsStatus.setDate(new Timestamp(new Date().getTime()));
            goodsStatus.setGoodsStatusName(goodsStatusNameDAO.findGoodsStatusNameByName(statusName));
            return goodsStatus;
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }

    }

    private User getAuthenticatedUser() throws ResourceNotFoundException, DataAccessException, IllegalParametersException {
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            Long userId = userDetails.getUserId();
            return userService.findUserById(userId);
        } else {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }
    }


    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void putGoodsInCells(Long goodsId, List<StorageCellDTO> storageCells) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Put goods in cells, goods id {}, cells: {}", goodsId, storageCells);
        if (goodsId == null) {
            throw new IllegalParametersException(ERROR_GOODS_ID_IS_NULL);
        }
        if (storageCells == null) {
            throw new IllegalParametersException("Storage cells id's list is null");
        }

        try {
            Goods goods = goodsDAO.getById(goodsId);
            if (goods == null) {
                throw new ResourceNotFoundException("Goods with id " + goodsId + " was not found");
            }

            //if status is not one of listed then goods cant be put in storage cells
            if (!isStorable(goods)) {
                return;
            }

            for (StorageCellDTO cell : storageCells) {
                StorageCell storageCell = findStorageCellById(cell.getIdStorageCell());
                if (storageCell != null) {
                    storageCell.setGoods(goods);
                }
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#goodsId, 'Goods', 'UPDATE')")
    public void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Remove goods from storage, id {}", goodsId);
        if (goodsId == null) {
            throw new IllegalParametersException(ERROR_GOODS_ID_IS_NULL);
        }

        try {
            Goods goods = goodsDAO.getById(goodsId);
            if (goods == null) {
                throw new ResourceNotFoundException("Goods with id " + goodsId + " was not found");
            }
            //if status is not stored then can not be removed from storage
            if (!isStored(goods)) {
                return;
            }
            List<StorageCell> cells = goods.getCells();
            for (StorageCell cell : cells) {
                cell.setGoods(null);
            }
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void setOutgoingInvoice(List<Long> goodsIds, Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Set outgoing invoice to goods, goods id's: {}, outgoing invoice id: {} ", goodsIds, invoiceId);
        if (CollectionUtils.isEmpty(goodsIds)) {
            throw new IllegalParametersException("Goods id's is empty");
        }
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }
        Invoice invoice = invoiceService.findInvoiceById(invoiceId);
        if (invoice != null) {
            for (Long id : goodsIds) {
                if (id != null) {
                    Goods goods = findGoodsById(id);
                    //if one of listed statuses set then cant be a part of outgoing invoice
                    if (isStored(goods)) {
                        goods.setOutgoingInvoice(invoice);
                        setGoodsStatus(goods.getId(), GoodsStatusEnum.WITHDRAWN);
                    }
                }
            }
        } else {
            throw new ResourceNotFoundException("Invoice with id " + invoiceId + " was not found");
        }
    }

    @Override
    @Transactional
    public List<Goods> updateAndGetGoodsForOutgoingInvoice(Long invoiceId, List<GoodsDTO> goodsList) throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Update and get goods for invoice, invoice id {}, list of goods: {}", invoiceId, goodsList);
        if (invoiceId == null) {
            throw new IllegalParametersException(ERROR_INVOICE_ID_IS_NULL);
        }
        if (goodsList == null) {
            throw new IllegalParametersException(ERROR_GOODS_IS_NULL);
        }

        List<Goods> goodsInInvoiceList = new ArrayList<>();
        try {
            for (GoodsDTO goodsDTO : goodsList) {
                if (goodsDTO != null) {
                    Goods initialGoods = findGoodsById(goodsDTO.getId());

                    if (goodsDTO.getQuantity().compareTo(initialGoods.getQuantity()) == 0) {
                        //if all amount is affected by act
                        goodsInInvoiceList.add(initialGoods);
                        setGoodsStatus(initialGoods.getId(), GoodsStatusEnum.WITHDRAWN);
                        removeGoodsFromStorage(initialGoods.getId());
                    } else {
                        Goods goodsInAct = createGoodsForInvoice(initialGoods, goodsDTO);
                        if (goodsInAct != null) {
                            setGoodsStatus(goodsInAct.getId(), GoodsStatusEnum.WITHDRAWN);
                            goodsInInvoiceList.add(goodsInAct);
                        }
                    }
                }
            }
            return goodsInInvoiceList;
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsStatusName> getStatusNames() throws DataAccessException {
        logger.info("Get status names list");
        try {
            return goodsStatusNameDAO.getStatusNames();
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuantityUnit> getQuantityUnits() throws DataAccessException {
        logger.info("Get quantity units list");
        try {
            return quantityUnitDAO.getQuantityUnits();
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeightUnit> getWeightUnits() throws DataAccessException {
        logger.info("Get weight units list");
        try {
            return weightUnitDAO.getWeightUnits();
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceUnit> getPriceUnits() throws DataAccessException {
        logger.info("Get price units list");
        try {
            return priceUnitDAO.getPriceUnits();
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);

        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageSpaceType> getStorageSpaceTypes() throws DataAccessException {
        logger.info("Get storage space types list");
        try {
            return storageSpaceTypeDAO.getStorageSpaceTypes();
        } catch (GenericDAOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    private StorageCell findStorageCellById(Long storageCellId) throws GenericDAOException, IllegalParametersException {
        logger.info("Find storage cell, id: {}", storageCellId);
        if (storageCellId == null) {
            throw new IllegalParametersException("Storage cell id is null");
        }

        Optional<StorageCell> result = storageCellDAO.findById(storageCellId);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
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
    public boolean isUpdatable(List<GoodsDTO> goodsDTOList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        for (GoodsDTO goodsDTO : goodsDTOList) {
            Goods goods = findGoodsById(goodsDTO.getId());
            if (!isUpdatable(goods)) {
                return false;
            }
        }
        return true;
    }

    private boolean isUpdatable(Goods goods) {
        return (hasAnyStatus(goods,
                GoodsStatusEnum.STORED,
                GoodsStatusEnum.REGISTERED,
                GoodsStatusEnum.WITHDRAWN));
    }

    private boolean isStorable(Goods goods) {
        return (hasAnyStatus(goods,
                GoodsStatusEnum.CHECKED,
                GoodsStatusEnum.STORED,
                GoodsStatusEnum.WITHDRAWN,
                GoodsStatusEnum.RELEASE_ALLOWED));
    }

    private boolean isStored(Goods goods) {
        return (hasStatus(goods, GoodsStatusEnum.STORED));
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


    private List<GoodsDTO> mapGoodsListToDTOList(List<Goods> goodsList) {
        Assert.notNull(goodsList, ERROR_GOODS_IS_NULL);

        List<GoodsDTO> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            dtoList.addAll(goodsList.stream().map(this::mapGoodsToDTO).collect(Collectors.toList()));
        }
        return dtoList;
    }

    private GoodsDTO mapGoodsToDTO(Goods goods) {
        Assert.notNull(goods, ERROR_GOODS_IS_NULL);

        GoodsDTO dto = new GoodsDTO();

        dto.setId(goods.getId());
        dto.setName(goods.getName());
        dto.setQuantity(goods.getQuantity());
        dto.setWeight(goods.getWeight());
        dto.setPrice(goods.getPrice());
        dto.setStorageType(goods.getStorageType());
        dto.setWeightUnit(goods.getWeightUnit());
        dto.setQuantityUnit(goods.getQuantityUnit());
        dto.setPriceUnit(goods.getPriceUnit());

        if (goods.getCurrentStatus() != null) {
            dto.setCurrentStatus(mapGoodsStatusToDTO(goods.getCurrentStatus()));
        }

        if (goods.getRegisteredStatus() != null) {
            dto.setRegisteredStatus(mapGoodsStatusToDTO(goods.getRegisteredStatus()));
        }

        if (goods.getMovedOutStatus() != null) {
            dto.setMovedOutStatus(mapGoodsStatusToDTO(goods.getMovedOutStatus()));
        }

        if (goods.getWarehouse() != null) {
            dto.setWarehouseId(goods.getWarehouse().getIdWarehouse());
        }

        List<StorageCellDTO> cellDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(goods.getCells())) {
            for (StorageCell cell : goods.getCells()) {
                StorageCellDTO cellDTO = new StorageCellDTO();
                cellDTO.setIdGoods(goods.getId());
                cellDTO.setIdStorageCell(cell.getIdStorageCell());
                cellDTO.setIdStorageSpace(cell.getStorageSpace().getIdStorageSpace());
                cellDTO.setNumber(cell.getNumber());
                cellDTOList.add(cellDTO);
            }
        }
        dto.setCells(cellDTOList);

        return dto;
    }


    private GoodsStatusDTO mapGoodsStatusToDTO(GoodsStatus status) {
        Assert.notNull(status, "Status is null");

        GoodsStatusDTO dto = new GoodsStatusDTO();

        dto.setId(status.getId());
        dto.setDate(status.getDate());
        dto.setNote(status.getNote());

        if (status.getGoodsStatusName() != null) {
            dto.setName(status.getGoodsStatusName().getName());
        }

        User user = new User();
        user.setId(status.getUser().getId());
        user.setLastName(status.getUser().getLastName());
        user.setFirstName(status.getUser().getFirstName());
        user.setPatronymic(status.getUser().getPatronymic());
        dto.setUser(user);

        return dto;
    }

    private List<GoodsStatusDTO> mapGoodsStatusListToDTOList(List<GoodsStatus> statuses) {
        Assert.notNull(statuses, "Statuses is null");
        List<GoodsStatusDTO> statusDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(statuses)) {
            statusDTOs.addAll(statuses.stream().map(this::mapGoodsStatusToDTO).collect(Collectors.toList()));
        }
        return statusDTOs;
    }

    private boolean hasStatus(Goods goods, GoodsStatusEnum status) {
        if (goods == null) {
            throw new IllegalArgumentException(ERROR_GOODS_IS_NULL);
        }
        if (status == null) {
            throw new IllegalArgumentException("Status name is null");
        }

        GoodsStatus currentStatus = goods.getCurrentStatus();
        return goods.getCurrentStatus() != null && (status.toString().equals(currentStatus.getGoodsStatusName().getName()));
    }

    private boolean hasAnyStatus(Goods goods, GoodsStatusEnum... statusNames) {
        if (goods == null) {
            throw new IllegalArgumentException(ERROR_GOODS_IS_NULL);
        }
        for (GoodsStatusEnum status : statusNames) {
            if (status != null && hasStatus(goods, status)) {
                return true;
            }
        }
        return false;

    }

    private boolean validateNewStatus(Goods goods, String newStatus) {
        if (goods == null) {
            throw new IllegalArgumentException(ERROR_GOODS_IS_NULL);
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status name is null");
        }
        if (goods.getCurrentStatus() == null) {
            return true;
        }

        GoodsStatusEnum currentStatus = GoodsStatusEnum.valueOf(goods.getCurrentStatus().getGoodsStatusName().getName());

        switch (currentStatus) {
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
            case WITHDRAWN:
                return !newStatus.equals(GoodsStatusEnum.CHECKED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.REGISTERED.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.TRANSPORT_COMPANY_MISMATCH.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.MOVED_OUT.toString()) &&
                        !newStatus.equals(GoodsStatusEnum.LOST_BY_TRANSPORT_COMPANY.toString());
            case RELEASE_ALLOWED:
                return newStatus.equals(GoodsStatusEnum.STORED.toString()) ||
                        newStatus.equals(GoodsStatusEnum.MOVED_OUT.toString());
            case STOLEN:
            case SEIZED:
            case TRANSPORT_COMPANY_MISMATCH:
            case LOST_BY_TRANSPORT_COMPANY:
            case LOST_BY_WAREHOUSE_COMPANY:
            case RECYCLED:
            case MOVED_OUT:
                return false;
            default:
                return false;
        }
    }

    private GoodsSearchCriteria convertGoodsSearchDTOToCriteria(GoodsSearchDTO dto) throws GenericDAOException {
        Assert.notNull(dto, "Search DTO is null");

        GoodsSearchCriteria criteria = new GoodsSearchCriteria();

        criteria.setName(dto.getName());
        criteria.setMinQuantity(dto.getMinQuantity());
        criteria.setMaxQuantity(dto.getMaxQuantity());
        criteria.setMinWeight(dto.getMinWeight());
        criteria.setMaxWeight(dto.getMaxWeight());
        criteria.setMinPrice(dto.getMinPrice());
        criteria.setMaxPrice(dto.getMaxPrice());

        if (StringUtils.isNotBlank(dto.getStorageType())) {
            criteria.setStorageType(storageSpaceTypeDAO.findStorageTypeByName(dto.getStorageType()));
        }
        if (StringUtils.isNotBlank(dto.getQuantityUnit())) {
            criteria.setQuantityUnit(quantityUnitDAO.findQuantityUnitByName(dto.getQuantityUnit()));
        }
        if (StringUtils.isNotBlank(dto.getWeightUnit())) {
            criteria.setWeightUnit(weightUnitDAO.findWeightUnitByName(dto.getWeightUnit()));
        }
        if (StringUtils.isNotBlank(dto.getPriceUnit())) {
            criteria.setPriceUnit(priceUnitDAO.findPriceUnitByName(dto.getPriceUnit()));
        }
        if (StringUtils.isNotBlank(dto.getCurrentStatus())) {
            criteria.setCurrentStatus(goodsStatusNameDAO.findGoodsStatusNameByName(dto.getCurrentStatus()));
        }

        criteria.setIncomingInvoiceId(dto.getIncomingInvoiceId());
        criteria.setOutgoingInvoiceId(dto.getOutgoingInvoiceId());
        criteria.setActApplicable(dto.getActApplicable());
        criteria.setActType(dto.getActType());

        if (CollectionUtils.isNotEmpty(dto.getStatuses())) {
            ArrayList<GoodsStatusSearchCriteria> statusCriteriaList = new ArrayList<>();
            for (GoodsStatusSearchDTO statusDTO : dto.getStatuses()) {
                GoodsStatusSearchCriteria statusCriteria = convertGoodsStatusSearchDTOToCriteria(statusDTO);
                statusCriteriaList.add(statusCriteria);
            }
            criteria.setStatuses(statusCriteriaList);
        }
        return criteria;
    }

    private GoodsStatusSearchCriteria convertGoodsStatusSearchDTOToCriteria(GoodsStatusSearchDTO dto) {
        Assert.notNull(dto, "Search DTO is null");

        GoodsStatusSearchCriteria criteria = new GoodsStatusSearchCriteria();

        criteria.setName(goodsStatusNameDAO.findGoodsStatusNameByName(dto.getName()));
        criteria.setUserLastName(dto.getUserLastName());
        criteria.setUserFirstName(dto.getUserFirstName());
        criteria.setUserPatronymic(dto.getUserPatronymic());
        criteria.setFromDate(dto.getFromDate());
        criteria.setToDate(dto.getToDate());

        return criteria;
    }

    private Goods updateRequiredFieldsFromDTO(Goods goodsToUpdate, GoodsDTO goodsDTO) throws IllegalParametersException, GenericDAOException {
        Assert.notNull(goodsToUpdate, ERROR_GOODS_IS_NULL);
        Assert.notNull(goodsDTO, ERROR_GOODS_DTO_IS_NULL);
        validateRequiredFields(goodsDTO);

        goodsToUpdate.setName(goodsDTO.getName());
        goodsToUpdate.setQuantity(goodsDTO.getQuantity());
        goodsToUpdate.setWeight(goodsDTO.getWeight());
        goodsToUpdate.setPrice(goodsDTO.getPrice());
        PriceUnit priceUnit = priceUnitDAO.findPriceUnitByName(goodsDTO.getPriceUnit().getName());
        goodsToUpdate.setPriceUnit(priceUnit);
        QuantityUnit quantityUnit = quantityUnitDAO.findQuantityUnitByName(goodsDTO.getQuantityUnit().getName());
        goodsToUpdate.setQuantityUnit(quantityUnit);
        WeightUnit weightUnit = weightUnitDAO.findWeightUnitByName(goodsDTO.getWeightUnit().getName());
        goodsToUpdate.setWeightUnit(weightUnit);
        StorageSpaceType storageSpaceType = storageSpaceTypeDAO.findStorageTypeByName(goodsDTO.getStorageType().getName());
        goodsToUpdate.setStorageType(storageSpaceType);

        return goodsToUpdate;
    }

    private Goods updateWarehouseField(Goods goodsToUpdate, GoodsDTO goodsDTO) throws IllegalParametersException, DataAccessException {
        Assert.notNull(goodsToUpdate, ERROR_GOODS_IS_NULL);
        Assert.notNull(goodsDTO, ERROR_GOODS_DTO_IS_NULL);

        if (goodsDTO.getWarehouseId() != null) {
            goodsToUpdate.setWarehouse(warehouseService.findWarehouseById(goodsDTO.getWarehouseId()));
        } else {
            throw new IllegalParametersException("Warehouse id can not be null");
        }

        return goodsToUpdate;
    }

    private Goods updateInvoiceField(Goods goodsToUpdate, Long invoiceId) throws DataAccessException, ResourceNotFoundException {
        Assert.notNull(goodsToUpdate, ERROR_GOODS_IS_NULL);
        Assert.notNull(invoiceId, ERROR_INVOICE_ID_IS_NULL);

        Invoice invoice = invoiceService.findInvoiceById(invoiceId);
        if (invoice != null) {
            goodsToUpdate.setIncomingInvoice(invoice);
        } else {
            throw new ResourceNotFoundException("Invoice with id " + invoiceId + " was not found");
        }

        return goodsToUpdate;
    }

    private void validateRequiredFields(GoodsDTO goodsDTO) throws IllegalParametersException {
        Assert.notNull(goodsDTO, ERROR_GOODS_DTO_IS_NULL);

        if (StringUtils.isBlank(goodsDTO.getName())) {
            throw new IllegalParametersException("Field name can not be empty");
        }

        if (goodsDTO.getQuantity() == null) {
            throw new IllegalParametersException("Field quantity can not be empty");
        }

        if (goodsDTO.getWeight() == null) {
            throw new IllegalParametersException("Field weight can not be empty");
        }

        if (goodsDTO.getPrice() == null) {
            throw new IllegalParametersException("Field price can not be empty");
        }

        if (goodsDTO.getPriceUnit() == null || goodsDTO.getPriceUnit().getName() == null) {
            throw new IllegalParametersException("Price unit can not be empty");
        }

        if (goodsDTO.getQuantityUnit() == null || goodsDTO.getQuantityUnit().getName() == null) {
            throw new IllegalParametersException("Quantity unit can not be empty");
        }

        if (goodsDTO.getWeightUnit() == null || goodsDTO.getWeightUnit().getName() == null) {
            throw new IllegalParametersException("Weight unit can not be empty");
        }

        if (goodsDTO.getStorageType() == null || goodsDTO.getStorageType().getName() == null) {
            throw new IllegalParametersException("Storage type can not be empty");
        }
    }
}