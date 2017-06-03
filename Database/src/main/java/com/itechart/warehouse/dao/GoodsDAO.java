package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.GoodsStatus;
import com.itechart.warehouse.entity.GoodsStatusName;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of goodsIdList DAO.
 */
@Repository
public class GoodsDAO extends DAO<Goods> {
    public GoodsDAO() {
        super(Goods.class);
    }

    @SuppressWarnings("unchecked")
    public Goods getById(Long id) throws GenericDAOException {
        logger.info("Find goods entity  with id: {}", id);
        if (id == null) return null;
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
                " ORDER BY goods.id";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
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
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL AND statusName.name = 'STORED'" +
                " ORDER BY goods.id ASC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
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
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL" +
                " AND statusName.name <> 'MOVED_OUT' AND statusName.name <> 'STOLEN' AND statusName.name <> 'SEIZED'" +
                " AND statusName.name <> 'TRANSPORT_COMPANY_MISMATCH' AND statusName.name <> 'RECYCLED'" +
                " AND statusName.name <> 'LOST_BY_WAREHOUSE_COMPANY' AND statusName.name IS NOT NULL" +
                " ORDER BY goods.id ASC";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }



    public List<Goods> findByWarehouseIdAndCurrentStatus(Long warehouseId, GoodsStatusName statusName, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by warehouse id: {} and status: {}", maxResults, firstResult, warehouseId, statusName);
        String queryHql = "SELECT goods FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON status.goods = goods" +
                " LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date" +
                " WHERE status_2.goods IS NULL AND warehouse.idWarehouse = :warehouseId AND status.goodsStatusName = :statusName AND goods.deleted IS NULL" +
                " ORDER BY goods.id ASC";

        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("statusName", statusName);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }




    public List<Goods> findByQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by query: {} with parameters", maxResults, firstResult, query, parameters);
        if (query == null || parameters == null) throw new AssertionError();
        Query<Goods> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        queryHQL.setFirstResult(firstResult);
        queryHQL.setMaxResults(maxResults);
        return queryHQL.list();
    }

    public long getCountByQuery(String query, Map<String, Object> parameters) throws GenericDAOException {
        logger.info("Get count of goods by query: {} with parameters", query, parameters);
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
        query.setParameter("warehouseId", warehouseId);
        return query.getSingleResult();
    }

    public long getStoredGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get stored goods count, warehouse id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON goods.currentStatus = status" +
                " INNER JOIN GoodsStatusName statusName ON status.goodsStatusName = statusName" +
                " WHERE warehouse.idWarehouse = :warehouseId AND goods.deleted IS NULL AND statusName.name = 'STORED'";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
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
                " AND statusName.name <> 'MOVED_OUT' AND statusName.name <> 'STOLEN' AND statusName.name <> 'SEIZED'" +
                " AND statusName.name <> 'TRANSPORT_COMPANY_MISMATCH' AND statusName.name <> 'RECYCLED'" +
                " AND statusName.name <> 'LOST_BY_WAREHOUSE_COMPANY' AND statusName.name IS NOT NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        return query.getSingleResult();
    }
}
