package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.User;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation of act DAO.
 */
@Repository
public class ActDAO extends DAO<Act> {
    public ActDAO() {
        super(Act.class);
    }

    public List<Act> findActsByWarehouseCompanyId(Long warehouseCompanyId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find {} acts starting from {} by warehouse company id: {}", maxResults, firstResult, warehouseCompanyId);
        String queryHql = "SELECT DISTINCT act" +
                " FROM Act act" +
                " INNER JOIN act.goods goods" +
                " INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " INNER JOIN WarehouseCompany company ON company = warehouse.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId";
        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public List<Act> findByGoodsId(Long goodsId) throws GenericDAOException {
        logger.info("Find list of acts for goods with id: {}", goodsId);

        String queryHql = "SELECT DISTINCT act FROM Act act" +
                " INNER JOIN act.goods goods" +
                " WHERE goods.id = :goodsId";
        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);
        return query.list();
    }

    public long getActsCount(Long warehouseCompanyId) throws GenericDAOException {
        logger.info("Get goods count for warehouse company with id: {}", warehouseCompanyId);
        String queryHql = "SELECT  count(DISTINCT act)" +
                " FROM Act act" +
                " INNER JOIN act.goods goods" +
                " INNER JOIN Invoice invoice ON goods.incomingInvoice = invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " INNER JOIN WarehouseCompany company ON company = warehouse.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        return query.getSingleResult();
    }


    public long getActsSearchCount(DetachedCriteria criteria) throws GenericDAOException{
        logger.info("Get acts count search result count");
        return ((List<Long>)hibernateTemplate.findByCriteria(criteria)).get(0);
    }

}
