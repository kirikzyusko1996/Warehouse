package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.entity.*;
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

import java.util.List;
import java.util.Optional;

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
    public List<Goods> findGoodsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods starting from index {} by company id: {}", maxResults, firstResult, companyId);
        if (companyId == null) throw new IllegalParametersException("Company id is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.add(Restrictions.eq("company_id", companyId));
        try {
            return goodsDAO.findAll(criteria, firstResult, maxResults);
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
    public List<Goods> findGoodsByCriteria(GoodsDTO goodsDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException {
        logger.info("Find {} goods starting from index {} by criteria: {}", maxResults, firstResult, goodsDTO);
        if (goodsDTO == null) throw new IllegalParametersException("Goods DTO is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        if (goodsDTO.getName() != null)
            criteria.add(Restrictions.eq("name", goodsDTO.getName()));
        if (goodsDTO.getQuantity() != null)
            criteria.add(Restrictions.eq("quantity", goodsDTO.getQuantity()));
        if (goodsDTO.getWeight() != null)
            criteria.add(Restrictions.eq("weight", goodsDTO.getWeight()));
        if (goodsDTO.getPrice() != null)
            criteria.add(Restrictions.eq("price", goodsDTO.getPrice()));
        if (goodsDTO.getStorageTypeName() != null)
            criteria.add(Restrictions.eq("storageType", goodsDTO.getStorageTypeName()));
        if (goodsDTO.getQuantityUnitName() != null)
            criteria.add(Restrictions.eq("quantityUnit", goodsDTO.getQuantityUnitName()));
        if (goodsDTO.getWeightUnitName() != null)
            criteria.add(Restrictions.eq("weightUnit", goodsDTO.getWeightUnitName()));
        if (goodsDTO.getPriceUnitName() != null)
            criteria.add(Restrictions.eq("priceUnit", goodsDTO.getPriceUnitName()));
        try {
            return goodsDAO.findAll(criteria, firstResult, maxResults);
        } catch (GenericDAOException e) {
            logger.error("Error during search for goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    public Goods updateGoods(Long id, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Updating goods with id {} from DTO: {}", id, goodsDTO);
        if (id == null || goodsDTO == null) throw new IllegalParametersException("Id or goods DTO is null");
        try {
            Goods goods = goodsDTO.buildGoodsEntity();
            goods.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
            goods.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
            goods.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
            goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
            return goodsDAO.update(goods);
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
        return fetchedUnits.get(1);
    }

    private StorageSpaceType findStorageTypeByName(String spaceTypeName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for storage space type with name: {}", spaceTypeName);
        if (spaceTypeName == null) throw new IllegalParametersException("Storage space type name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        criteria.add(Restrictions.eq("name", spaceTypeName));
        List<StorageSpaceType> fetchedSpaceType = storageSpaceTypeDAO.findAll(criteria, -1, 1);
        return fetchedSpaceType.get(1);
    }

    @Override
    @Transactional
    public Goods createGoods(GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException {
        logger.info("Creating goods from DTO: {}", goodsDTO);
        if (goodsDTO == null) throw new IllegalParametersException("Goods DTO is null");
        try {
            //todo set invoice??
            Goods goods = goodsDTO.buildGoodsEntity();
            goods.setPriceUnit(findUnitByName(goodsDTO.getPriceUnitName()));
            goods.setQuantityUnit(findUnitByName(goodsDTO.getQuantityUnitName()));
            goods.setWeightUnit(findUnitByName(goodsDTO.getWeightUnitName()));
            goods.setStorageType(findStorageTypeByName(goodsDTO.getStorageTypeName()));
            GoodsStatus goodsStatus = new GoodsStatus();
            goodsStatus.setGoodsStatusName(findGoodsStatusNameByName(GoodsStatusEnum.REGISTERED.toString()));
            goods.addStatus(goodsStatus);
            return goodsDAO.insert(goods);
        } catch (GenericDAOException e) {
            logger.error("Error during saving goods: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    private GoodsStatusName findGoodsStatusNameByName(String goodsStatusNameName) throws GenericDAOException, IllegalParametersException {
        logger.info("Searching for goods status name with name: {}", goodsStatusNameName);
        if (goodsStatusNameName == null) throw new IllegalParametersException("Goods status name name is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        criteria.add(Restrictions.eq("name", goodsStatusNameName));
        List<GoodsStatusName> fetchedStatusName = goodsStatusNameDAO.findAll(criteria, -1, 1);
        return fetchedStatusName.get(1);
    }

    @Override
    @Transactional
    public void deleteGoods(Long id) throws DataAccessException, IllegalParametersException {
        logger.info("Deleting goods with id: {}", id);
        if (id == null) throw new IllegalParametersException("Id is null");
        try {
            Optional<Goods> result = goodsDAO.findById(id);
            if (result != null)
                goodsDAO.delete(result.get());
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
                // TODO: 26.04.2017 do I need to use update method or hibernate will update automatically
//            storageCellDAO.update(storageCell);
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
}
