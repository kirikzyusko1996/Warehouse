package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.entity.Warehouse;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jpa.HibernateQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
                " INNER JOIN StorageCell cell ON cell.goods = goods" +
                " INNER JOIN StorageSpace space ON cell.storageSpace = space" +
                " INNER JOIN Warehouse warehouse ON space.warehouse = warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId";
        Query<Goods> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public List<Goods> findByExample(Goods goods, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find list of {} goods starting from {} by example: {}", maxResults, firstResult, goods);
        return hibernateTemplate.findByExample(goods, firstResult, maxResults);
    }

}
