package com.itechart.warehouse.dao;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.GoodsStatusName;
import com.itechart.warehouse.query.GoodsSearchCriteria;
import com.itechart.warehouse.query.GoodsSearchQueryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Implementation of goodsIdList DAO.
 */
@Repository
public class GoodsDAO extends DAO<Goods> {
    public GoodsDAO() {
        super(Goods.class);
    }

    private static final String PARAMETER_WAREHOUSE_ID = "warehouseId";

    public List<Goods> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchCriteria goodsSearchCriteria, int firstResult, int maxResults) throws GenericDAOException {
        GoodsSearchQueryBuilder builder = new GoodsSearchQueryBuilder(warehouseId, goodsSearchCriteria);
        builder.buildListQuery();
        return findByQuery(builder.getQuery(), builder.getParameters(), firstResult, maxResults);
    }

    public long getGoodsSearchResultCount(Long warehouseId, GoodsSearchCriteria goodsSearchCriteria) throws GenericDAOException {
        GoodsSearchQueryBuilder builder = new GoodsSearchQueryBuilder(warehouseId, goodsSearchCriteria);
        builder.buildCountQuery();
        return getCountByQuery(builder.getQuery(), builder.getParameters());
    }


    public List<Goods> findGoodsForIncomingInvoice(Long invoiceId, int firstResult, int maxResults) throws GenericDAOException {
        Assert.notNull(invoiceId, "Invoice id is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.createAlias("incomingInvoice", "invoice");
        criteria.add(Restrictions.eq("invoice.id", invoiceId));
        criteria.add(Restrictions.isNull("deleted"));
        criteria.addOrder(Order.desc("id"));
        return super.findAll(criteria, firstResult, maxResults);
    }


    public List<Goods> findGoodsForOutgoingInvoice(Long invoiceId, int firstResult, int maxResults) throws GenericDAOException {
        Assert.notNull(invoiceId, "Invoice id is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.createAlias("outgoingInvoice", "invoice");
        criteria.add(Restrictions.eq("invoice.id", invoiceId));
        criteria.add(Restrictions.isNull("deleted"));
        criteria.addOrder(Order.desc("id"));
        return super.findAll(criteria, firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    public Goods getById(Long id) throws GenericDAOException {
        logger.info("Find goods, id: {}", id);
        Assert.notNull(id, "Id is null");
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.isNull("deleted"));
        List<Goods> foundGoods = (List<Goods>) hibernateTemplate.findByCriteria(criteria);
        return CollectionUtils.isNotEmpty(foundGoods) ? foundGoods.get(0) : null;
    }

    public List<Goods> findByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL" +
                " ORDER BY goods.id DESC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public List<Goods> findStoredGoodsByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find stored goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);

        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL AND statusName.name = " + getStatusName(GoodsStatusEnum.STORED) +
                " ORDER BY goods.id DESC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public List<Goods> findApplicableToActGoodsByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods applicable to act, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);

        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName" +
                " WHERE warehouse.idWarehouse = :warehouseId" +
                " AND goods.deleted IS NULL" +
                " AND (statusName.name = " + getStatusName(GoodsStatusEnum.REGISTERED) +
                " OR statusName.name = " + getStatusName(GoodsStatusEnum.STORED) +
                " OR statusName.name = " + getStatusName(GoodsStatusEnum.WITHDRAWN) + ")" +
                " AND statusName.name IS NOT NULL" +
                " ORDER BY goods.id DESC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public List<Goods> findByWarehouseIdAndCurrentStatus(Long warehouseId, GoodsStatusName statusName, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by warehouse id: {} and status: {}", maxResults, firstResult, warehouseId, statusName);
        String queryHql = "SELECT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " WHERE warehouse.idWarehouse = :warehouseId AND status.goodsStatusName = :statusName AND goods.deleted IS NULL" +
                " ORDER BY goods.id DESC";

        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("statusName", statusName);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public List<Goods> findByQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, first result: {}, max results: {},  query: {}, parameters: {}", firstResult, maxResults, query, parameters);
        if (query == null || parameters == null) {
            throw new AssertionError();
        }

        Query<Goods> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        queryHQL.setFirstResult(firstResult);
        queryHQL.setMaxResults(maxResults);
        return queryHQL.list();
    }

    public long getCountByQuery(String query, Map<String, Object> parameters) throws GenericDAOException {
        logger.info("Get count of goods, query: {}, parameters", query, parameters);
        if (query == null || parameters == null) throw new AssertionError();
        Query<Long> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        return queryHQL.getSingleResult();
    }

    public long getGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods count, warehouse id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        return query.getSingleResult();
    }

    public long getStoredGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get stored goods count, warehouse id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL AND statusName.name = " + getStatusName(GoodsStatusEnum.STORED);
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        return query.getSingleResult();
    }

    public long getApplicableToActGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods applicable to act count, warehouse id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL" +
                " AND goods.deleted IS NULL" +
                " AND (statusName.name = " + getStatusName(GoodsStatusEnum.REGISTERED) +
                " OR statusName.name = " + getStatusName(GoodsStatusEnum.STORED) +
                " OR statusName.name = " + getStatusName(GoodsStatusEnum.WITHDRAWN) + ")" +
                " AND statusName.name IS NOT NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        return query.getSingleResult();
    }

    private String getStatusName(GoodsStatusEnum status) {
        Assert.notNull(status, "Status is null");
        return "'" + status.toString() + "'";
    }
}
