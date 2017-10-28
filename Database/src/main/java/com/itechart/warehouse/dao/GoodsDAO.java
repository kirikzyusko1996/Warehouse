package com.itechart.warehouse.dao;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.GoodsStatusName;
import com.itechart.warehouse.query.GoodsSearchCriteria;
import com.itechart.warehouse.query.GoodsSearchQueryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String PARAMETER_WAREHOUSE_COMPANY_ID = "warehouseCompanyId";
    private static final String DELETED = "deleted";

    public List<Goods> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchCriteria goodsSearchCriteria, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, first result: {}, max results: {}, warehouse id: {}, criteria: {}", firstResult, maxResults, warehouseId, goodsSearchCriteria);
        Assert.notNull(warehouseId, "Warehouse id is null");
        Assert.notNull(goodsSearchCriteria, "Search criteria id is null");

        GoodsSearchQueryBuilder builder = new GoodsSearchQueryBuilder(warehouseId, goodsSearchCriteria);
        builder.buildListQuery();
        return findByQuery(builder.getQuery(), builder.getParameters(), firstResult, maxResults);
    }

    public long getGoodsSearchResultCount(Long warehouseId, GoodsSearchCriteria goodsSearchCriteria) throws GenericDAOException {
        logger.info("Find goods count, warehouse id: {}, criteria: {}", warehouseId, goodsSearchCriteria);
        Assert.notNull(warehouseId, "Warehouse id is null");
        Assert.notNull(goodsSearchCriteria, "Search criteria id is null");

        GoodsSearchQueryBuilder builder = new GoodsSearchQueryBuilder(warehouseId, goodsSearchCriteria);
        builder.buildCountQuery();
        return getCountByQuery(builder.getQuery(), builder.getParameters());
    }


    public List<Goods> findGoodsForIncomingInvoice(Long invoiceId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, first result: {}, max results: {}, incoming invoice id: {}", firstResult, maxResults, invoiceId);
        Assert.notNull(invoiceId, "Invoice id is null");

        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.createAlias("incomingInvoice", "invoice");
        criteria.add(Restrictions.eq("invoice.id", invoiceId));
        criteria.add(Restrictions.isNull(DELETED));
        criteria.addOrder(Order.desc("id"));
        return super.findAll(criteria, firstResult, maxResults);
    }


    public List<Goods> findGoodsForOutgoingInvoice(Long invoiceId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, first result: {}, max results: {}, outgoing invoice id: {}", firstResult, maxResults, invoiceId);
        Assert.notNull(invoiceId, "Invoice id is null");

        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.createAlias("outgoingInvoice", "invoice");
        criteria.add(Restrictions.eq("invoice.id", invoiceId));
        criteria.add(Restrictions.isNull(DELETED));
        criteria.addOrder(Order.desc("id"));
        return super.findAll(criteria, firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    public Goods getById(Long id) throws GenericDAOException {
        logger.info("Find goods, id: {}", id);
        Assert.notNull(id, "Id is null");

        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.isNull(DELETED));
        List<Goods> foundGoods = (List<Goods>) hibernateTemplate.findByCriteria(criteria);
        return CollectionUtils.isNotEmpty(foundGoods) ? foundGoods.get(0) : null;
    }

    public List<Goods> findByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        Assert.notNull(warehouseId, "Warehouse id is null");

        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL" +
                " ORDER BY goods.id DESC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        if (firstResult != -1 && maxResults != -1) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }
        return query.list();
    }

    public List<Goods> findByWarehouseCompanyId(Long warehouseCompanyId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find goods, warehouse company id: {}, first result {}, max results: {}", warehouseCompanyId, firstResult, maxResults);
        Assert.notNull(warehouseCompanyId, "Warehouse company id is null");

        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse " +
                " INNER JOIN WarehouseCompany company ON warehouse.warehouseCompany = company" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND goods.deleted IS NULL" +
                " ORDER BY goods.id DESC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_COMPANY_ID, warehouseCompanyId);
        if (firstResult != -1 && maxResults != -1) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }
        return query.list();
    }

    public List<Goods> findStoredGoodsByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find stored goods, warehouse id: {}, first result {}, max results: {}", warehouseId, firstResult, maxResults);
        Assert.notNull(warehouseId, "Warehouse id is null");

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
        Assert.notNull(warehouseId, "Warehouse id is null");

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
        logger.info("Find goods, first result: {}, max results: {}, warehouse id: {}, status: {}", firstResult, maxResults, warehouseId, statusName);
        Assert.notNull(warehouseId, "Warehouse id is null");

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
        Assert.notNull(query, "Query id is null");
        Assert.notNull(parameters, "Parameters is null");

        Query<Goods> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        queryHQL.setFirstResult(firstResult);
        queryHQL.setMaxResults(maxResults);
        return queryHQL.list();
    }

    public long getCountByQuery(String query, Map<String, Object> parameters) throws GenericDAOException {
        logger.info("Get count of goods, query: {}, parameters: {}", query, parameters);
        Assert.notNull(query, "Query id is null");
        Assert.notNull(parameters, "Parameters is null");

        if (query == null || parameters == null) throw new AssertionError();
        Query<Long> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        return queryHQL.getSingleResult();
    }

    public long getGoodsCountByWarehouse(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods count, warehouse id: {}", warehouseId);
        Assert.notNull(warehouseId, "Warehouse id is null");

        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_ID, warehouseId);
        return query.getSingleResult();
    }

    public long getGoodsCountByWarehouseCompany(Long warehouseCompanyId) throws GenericDAOException {
        logger.info("Get goods count, warehouse company id: {}", warehouseCompanyId);
        Assert.notNull(warehouseCompanyId, "Warehouse company id is null");

        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse " +
                " INNER JOIN WarehouseCompany compnay ON warehouse.warehouseCompany = compnay" +
                " WHERE compnay.idWarehouseCompany = :warehouseCompanyId AND goods.deleted IS NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter(PARAMETER_WAREHOUSE_COMPANY_ID, warehouseCompanyId);
        return query.getSingleResult();
    }

    public long getStoredGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get stored goods count, warehouse id: {}", warehouseId);
        Assert.notNull(warehouseId, "Warehouse id is null");

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
        Assert.notNull(warehouseId, "Warehouse id is null");

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

    /**
     * {@inheritDoc}
     */
    @Transactional
    public Goods getGoodsByIdWithInvoices(Long goodsId) {
        Goods goods = hibernateTemplate.get(Goods.class, goodsId);
        /**
         * Since by default the toys are not loaded, we call the hibernate
         * template's initialize method to populate the toys list of that
         * respective child.
         */
        hibernateTemplate.initialize(goods.getOutgoingInvoice());
        hibernateTemplate.initialize(goods.getIncomingInvoice());
        return goods;
    }

    private String getStatusName(GoodsStatusEnum status) {
        Assert.notNull(status, "Status is null");
        return "'" + status.toString() + "'";
    }
}
