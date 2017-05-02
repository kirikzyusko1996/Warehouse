package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.GoodsService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of goodsList service.
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
    public Goods findGoodsById(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Find goods by id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            return result.get();
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    public List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods starting from index {} by invoice id: {}", maxResults, firstResult, invoiceId);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        List<Goods> goods = null;
        try {
            Optional<Invoice> result = invoiceDAO.findById(invoiceId);
            Invoice invoice = result.get();
            if ((goods = invoice.getIncomingGoods()).isEmpty()) {
                goods = invoice.getOutgoingGoods();
            }
            return goods;
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods for warehouse with id {} starting from index {} by criteria: {}", maxResults, warehouseId, firstResult, goodsSearchDTO);
        if (goodsSearchDTO == null || warehouseId == null)
            throw new IllegalParametersException("Goods search DTO or warehouse id is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        // TODO: 30.04.2017 find for warehouse
//        criteria.add(Restrictions.eq("company_id", companyId));
        if (goodsSearchDTO.getName() != null)
            criteria.add(Restrictions.like("name", goodsSearchDTO.getName()));
        if (goodsSearchDTO.getQuantity() != null)
            criteria.add(Restrictions.eq("quantity", goodsSearchDTO.getQuantity()));
        if (goodsSearchDTO.getWeight() != null)
            criteria.add(Restrictions.eq("weight", goodsSearchDTO.getWeight()));
        if (goodsSearchDTO.getPrice() != null)
            criteria.add(Restrictions.eq("price", goodsSearchDTO.getPrice()));
        if (goodsSearchDTO.getStorageTypeName() != null)
            criteria.add(Restrictions.eq("storageType", goodsSearchDTO.getStorageTypeName()));
        if (goodsSearchDTO.getQuantityUnitName() != null)
            criteria.add(Restrictions.eq("quantityUnit", goodsSearchDTO.getQuantityUnitName()));
        if (goodsSearchDTO.getWeightUnitName() != null)
            criteria.add(Restrictions.eq("weightUnit", goodsSearchDTO.getWeightUnitName()));
        if (goodsSearchDTO.getPriceUnitName() != null)
            criteria.add(Restrictions.eq("priceUnit", goodsSearchDTO.getPriceUnitName()));

        //todo add criterias: status, date...
        try {
            return goodsDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<Goods> findGoodsForCompanyByStatus(Long companyId, String statusName, java.sql.Date fromDate, java.sql.Date toDate, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        return null;
    }

    //todo find by status between dates

    @Override
    @Transactional
    public Goods updateGoods(Long id, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Updating goods with id {} from DTO: {}", id, goodsDTO);
        if (id == null || goodsDTO == null) throw new IllegalParametersException("Id or goods DTO is null");
        try {
            Goods goodsToUpdate = findGoodsById(id);
            if (goodsDTO.getName() != null)
                goodsToUpdate.setName(goodsDTO.getName());
            if (goodsDTO.getQuantity() != null)
                goodsToUpdate.setQuantity(goodsDTO.getQuantity());
            if (goodsDTO.getWeight() != null)
                goodsToUpdate.setWeight(goodsDTO.getWeight());
            if (goodsDTO.getPrice() != null)
                goodsToUpdate.setPrice(goodsDTO.getPrice());
            if (goodsDTO.getStorageTypeName() != null)
                goodsToUpdate.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
            if (goodsDTO.getQuantityUnitName() != null)
                goodsToUpdate.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
            if (goodsDTO.getWeightUnitName() != null)
                goodsToUpdate.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
            if (goodsDTO.getPriceUnitName() != null)
                goodsToUpdate.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
            return goodsDAO.update(goodsToUpdate);
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
        return fetchedUnits.get(0);
    }

    private StorageSpaceType findStorageTypeByName(String spaceTypeName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for storage space type with name: {}", spaceTypeName);
        if (spaceTypeName == null) throw new IllegalParametersException("Storage space type name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        criteria.add(Restrictions.eq("name", spaceTypeName));
        List<StorageSpaceType> fetchedSpaceType = storageSpaceTypeDAO.findAll(criteria, -1, 1);
        return fetchedSpaceType.get(0);
    }

    @Override
    @Transactional
    public Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Creating goods for invoice wuth id {} from DTO: {}", invoiceId, goodsDTO);
        if (invoiceId == null || goodsDTO == null)
            throw new IllegalParametersException("Invoice id or goods DTO is null");
        try {
            Goods goods = goodsDTO.buildGoodsEntity();
            if (goodsDTO.getPriceUnitName() != null)
                goods.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
            if (goodsDTO.getQuantityUnitName() != null)
                goods.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
            if (goodsDTO.getWeightUnitName() != null)
                goods.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
            if (goodsDTO.getStorageTypeName() != null)
                goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
            goods.setIncomingInvoice(findInvoiceById(invoiceId));

            Goods savedGoods = goodsDAO.insert(goods);

            GoodsStatus goodsStatus = new GoodsStatus();
            goodsStatus.setGoods(savedGoods);
            goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(GoodsStatusEnum.REGISTERED.toString()));
            goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
            goodsStatus.setDate(new Timestamp(new Date().getTime()));
            return savedGoods;
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    public List<Goods> createGoodsBatch(Long invoiceId, List<GoodsDTO> goodsDtoList) throws DataAccessException, IllegalParametersException {
        return null;
    }

    private Invoice findInvoiceById(Long invoiceId) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for invoice with id: {}", invoiceId);
        if (invoiceId == null) throw new IllegalParametersException("Invoice id is null");
        Optional<Invoice> result = invoiceDAO.findById(invoiceId);
        return result.get();
    }

    private User findUserById(Long userId) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for user with id: {}", userId);
        if (userId == null) throw new IllegalParametersException("User id is null");
        Optional<User> result = userDAO.findById(userId);
        return result.get();
    }

    private GoodsStatusName findGoodsStatusNameByName(String goodsStatusNameName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for goods status name with name: {}", goodsStatusNameName);
        if (goodsStatusNameName == null) throw new IllegalParametersException("Goods status name name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        criteria.add(Restrictions.eq("name", goodsStatusNameName));
        List<GoodsStatusName> fetchedStatusName = goodsStatusNameDAO.findAll(criteria, -1, 1);
        return fetchedStatusName.get(0);
    }

    @Override
    @Transactional
    public void deleteGoods(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Deleting goods with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            if (result != null) {
                removeGoodsFromStorage(id);
                goodsDAO.delete(result.get());
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
    public void setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Setting status: {} to goods with id {}", goodsStatusDTO, goodsId);
        if (goodsId == null || goodsStatusDTO == null)
            throw new IllegalParametersException("Goods status DTO or goods id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            Goods goods = result.get();
            GoodsStatus goodsStatus = new GoodsStatus();
            goodsStatus.setGoods(goods);
            goodsStatus.setDate(new Timestamp(new Date().getTime()));
            goodsStatus.setUser(findUserById(UserDetailsProvider.getUserDetails().getUserId()));
            goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(goodsStatusDTO.getStatusName()));
            goodsStatusDAO.insert(goodsStatus);
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public void putGoodsInCells(Long goodsId, List<Long> storageCellIds) throws DataAccessException, IllegalParametersException {
        logger.info("Putting goods with id {} in cells with ids: {}", goodsId, storageCellIds);
        if (goodsId == null || storageCellIds == null)
            throw new IllegalParametersException("Goods id or storage cell id's list is null");
        try {
            for (Long id : storageCellIds) {
                StorageCell storageCell = findStorageCellById(id);
                Goods goods = findGoodsById(goodsId);
                storageCell.setGoods(goods);
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
        return result.get();
    }


    @Override
    @Transactional
    public void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException {
        logger.info("Removing goods with id {} from storage", goodsId);
        if (goodsId == null)
            throw new IllegalParametersException("Goods id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(goodsId);
            Goods goods = result.get();
            List<StorageCell> cells = goods.getCells();
            for (StorageCell cell : cells) {
                cell.setGoods(null);
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public void setOutgoingInvoice(List<Long> goodsIds, Long invoiceId) throws DataAccessException, IllegalParametersException {
        logger.info("Setting to goods with id's: {} outgoing invoice {} ", goodsIds, invoiceId);
        if (goodsIds == null || invoiceId == null)
            throw new IllegalParametersException("Goods id's or invoice id is null");
        try {
            for (Long id : goodsIds) {
                if (id != null) {
                    Goods goods = goodsDAO.findById(id).get();
                    if (goods!=null)
                        goods.setOutgoingInvoice(findInvoiceById(invoiceId));
                }
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods status: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

}
