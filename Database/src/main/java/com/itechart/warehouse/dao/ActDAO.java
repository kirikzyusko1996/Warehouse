package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Act;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
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


    public long getCount(DetachedCriteria criteria) {
        logger.info("Find count of entities by criteria");
        hibernateTemplate.findByCriteria(criteria);
        List<Long> list = (List<Long>) hibernateTemplate.findByCriteria(criteria);
        if (CollectionUtils.isNotEmpty(list))
            return list.get(0);
        else return 0;
    }

    @SuppressWarnings("unchecked")
    public Act getById(Long id) throws GenericDAOException {
        logger.info("Find act entity  with id: {}", id);
        if (id == null) return null;
        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.isNull("deleted"));
        List<Act> foundActs = (List<Act>) hibernateTemplate.findByCriteria(criteria);
        return CollectionUtils.isNotEmpty(foundActs) ? foundActs.get(0) : null;
    }

    public List<Act> findActsByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find {} acts starting from {} by warehouse warehouse id: {}", maxResults, firstResult, warehouseId);
        String queryHql = "SELECT DISTINCT act" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND act.deleted IS NULL";
        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public List<Act> findByGoodsId(Long goodsId) throws GenericDAOException {
        logger.info("Find list of acts for goods with id: {}", goodsId);

        String queryHql = "SELECT DISTINCT act FROM Act act" +
                " INNER JOIN act.goods goods" +
                " WHERE goods.id = :goodsId AND act.deleted IS NULL";
        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);
        return query.list();
    }

    public long getActsCount(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods count for warehouse company with id: {}", warehouseId);
        String queryHql = "SELECT  count(DISTINCT act)" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND act.deleted IS NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        return query.getSingleResult();
    }


    public long getActsSearchCount(DetachedCriteria criteria) throws GenericDAOException {
        logger.info("Get acts count search result count");
        return ((List<Long>) hibernateTemplate.findByCriteria(criteria)).get(0);
    }

}
