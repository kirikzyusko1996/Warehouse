package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.GoodsStatus;
import com.itechart.warehouse.entity.GoodsStatusName;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Implementation of goodsList DAO.
 */
@Repository
public class GoodsDAO extends DAO<Goods> {
    public GoodsDAO() {
        super(Goods.class);
    }

    public List<Goods> findByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by warehouse id: {}", maxResults, firstResult, warehouseId);

        String queryHql = "SELECT DISTINCT goods FROM Goods goods" +
                " INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice" +
                " INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public List<Goods> findByWarehouseIdAndCurrentStatus(Long warehouseId, GoodsStatusName statusName, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by warehouse id: {} and status: {}", maxResults, firstResult, warehouseId, statusName);
        String queryHql = "SELECT goods FROM Goods goods" +
                " INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice" +
                " INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse" +
                " INNER JOIN GoodsStatus status ON status.goods = goods" +
                " LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date" +
                " WHERE status_2.goods IS NULL AND warehouse.idWarehouse = :warehouseId AND status.goodsStatusName = :statusName";

        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("statusName", statusName);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public GoodsStatus findGoodsCurrentStatus(Long goodsId) throws GenericDAOException {
        logger.info("Find current status of goods with id: {}", goodsId);
        String queryHql = "SELECT status FROM GoodsStatus status" +
                " INNER JOIN Goods goods ON goods = status.goods" +
                " LEFT OUTER JOIN GoodsStatus status_2 ON status.goods = status_2.goods AND status.date < status_2.date" +
                " WHERE status_2.goods IS NULL AND goods.id = :goodsId";
        Query<GoodsStatus> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error getting current status: {}", e);
            throw new GenericDAOException(e);
        }
    }


    public List<Goods> findByExample(Goods goods, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by example: {}", maxResults, firstResult, goods);
        return hibernateTemplate.findByExample(goods, firstResult, maxResults);
    }

    public List<Goods> findByQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by example: {}", maxResults, firstResult);
        if (query == null || parameters == null) throw new AssertionError();
        Query<Goods> queryHQL = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(query);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            queryHQL.setParameter(entry.getKey(), entry.getValue());
        }
        queryHQL.setFirstResult(firstResult);
        queryHQL.setMaxResults(maxResults);
        return queryHQL.list();
    }

    public long getGoodsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods count for warehouse with id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT goods)" +
                " FROM Goods goods" +
                " INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice" +
                " INNER JOIN Warehouse warehouse ON invoice.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        return query.getSingleResult();
    }
}
