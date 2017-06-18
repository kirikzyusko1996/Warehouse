package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.query.ActSearchCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementation of act DAO.
 */
@Repository
public class ActDAO extends DAO<Act> {

    private static final String ERROR_WAREHOUSE_ID_IS_NULL = "Warehouse id is null";
    private static final String ERROR_WAREHOUSE_COMPANY_ID_IS_NULL = "Warehouse company id is null";


    private static final String DELETED = "deleted";

    public ActDAO() {
        super(Act.class);
    }


    public List<Act> findActsForWarehouseCompanyByCriteria(Long warehouseCompanyId, ActSearchCriteria searchCriteria, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse company id: {}, criteria: {}", firstResult, maxResults, warehouseCompanyId, searchCriteria);
        Assert.notNull(warehouseCompanyId, ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        Assert.notNull(searchCriteria, "Search criteria id is null");

        DetachedCriteria criteria = buildHibernateCriteria(searchCriteria);
        addWarehouseRestriction(warehouseCompanyId, criteria);

        criteria.setProjection(Projections.distinct(Projections.id()));
        DetachedCriteria criteriaWithSubquery = DetachedCriteria.forClass(Act.class);
        criteriaWithSubquery.add(Subqueries.propertyIn("id", criteria));
        criteriaWithSubquery.addOrder(Order.desc("date"));

        return findAll(criteriaWithSubquery, firstResult, maxResults);
    }

    public long getCountOfActsForWarehouseCompanyByCriteria(Long warehouseCompanyId, ActSearchCriteria searchCriteria) throws GenericDAOException {
        logger.info("Get acts count, warehouse company id: {}, criteria: {}", warehouseCompanyId, searchCriteria);
        Assert.notNull(warehouseCompanyId, ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);
        Assert.notNull(searchCriteria, "Search criteria id is null");

        DetachedCriteria criteria = buildHibernateCriteria(searchCriteria);
        addWarehouseRestriction(warehouseCompanyId, criteria);

        criteria.setProjection(Projections.distinct(Projections.id()));
        criteria.setProjection(Projections.rowCount());

        return getCount(criteria);
    }


    public List<Act> findActsForWarehouseByCriteria(Long warehouseId, ActSearchCriteria searchCriteria, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse id: {}, criteria: {}", firstResult, maxResults, warehouseId, searchCriteria);
        Assert.notNull(warehouseId, ERROR_WAREHOUSE_ID_IS_NULL);
        Assert.notNull(searchCriteria, "Search criteria id is null");

        DetachedCriteria criteria = buildHibernateCriteria(searchCriteria);
        addWarehouseRestriction(warehouseId, criteria);
        criteria.setProjection(Projections.distinct(Projections.id()));
        DetachedCriteria criteriaWithSubquery = DetachedCriteria.forClass(Act.class);
        criteriaWithSubquery.add(Subqueries.propertyIn("id", criteria));
        criteriaWithSubquery.addOrder(Order.desc("date"));

        return findAll(criteriaWithSubquery, firstResult, maxResults);
    }

    public long getCountOfActsForWarehouseByCriteria(Long warehouseId, ActSearchCriteria searchCriteria) throws GenericDAOException {
        logger.info("Get acts count, warehouse id: {}, criteria: {}", warehouseId, searchCriteria);
        Assert.notNull(warehouseId, ERROR_WAREHOUSE_ID_IS_NULL);
        Assert.notNull(searchCriteria, "Search criteria id is null");

        DetachedCriteria criteria = buildHibernateCriteria(searchCriteria);
        addWarehouseRestriction(warehouseId, criteria);
        criteria.setProjection(Projections.distinct(Projections.id()));
        criteria.setProjection(Projections.rowCount());

        return getCount(criteria);
    }

    private DetachedCriteria buildHibernateCriteria(ActSearchCriteria searchCriteria) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        if (searchCriteria.getType() != null) {
            criteria.add(Restrictions.eq("actType", searchCriteria.getType()));
        }
        if (searchCriteria.getFromDate() != null) {
            criteria.add(Restrictions.ge("date", searchCriteria.getFromDate()));
        }
        if (searchCriteria.getToDate() != null) {
            criteria.add(Restrictions.le("date", new Timestamp(new DateTime(searchCriteria.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime())));
        }
        criteria.createAlias("user", "user");
        if (StringUtils.isNotBlank(searchCriteria.getCreatorLastName())) {
            criteria.add(Restrictions.like("user.lastName", "%" + searchCriteria.getCreatorLastName() + "%"));
        }
        if (StringUtils.isNotBlank(searchCriteria.getCreatorFirstName())) {
            criteria.add(Restrictions.like("user.firstName", "%" + searchCriteria.getCreatorFirstName() + "%"));
        }
        if (StringUtils.isNotBlank(searchCriteria.getCreatorPatronymic())) {
            criteria.add(Restrictions.like("user.patronymic", "%" + searchCriteria.getCreatorPatronymic() + "%"));
        }

        criteria.add(Restrictions.isNull(DELETED));
        return criteria;
    }

    private DetachedCriteria addWarehouseRestriction(Long warehouseId, DetachedCriteria criteria) {
        criteria
                .createCriteria("warehouse").add(Restrictions.eq("idWarehouse", warehouseId));
        return criteria;
    }

    private DetachedCriteria addWarehouseCompanyRestriction(Long warehouseCompanyId, DetachedCriteria criteria) {
        criteria.createAlias("warehouse", "warehouse")
                .createCriteria("warehouseCompany").add(Restrictions.eq("idWarehouseCompany", warehouseCompanyId));
        return criteria;
    }

    public long getCount(DetachedCriteria criteria) {
        logger.info("Find count of acts");

        hibernateTemplate.findByCriteria(criteria);
        List<Long> list = (List<Long>) hibernateTemplate.findByCriteria(criteria);
        if (CollectionUtils.isNotEmpty(list))
            return list.get(0);
        else return 0;
    }

    @SuppressWarnings("unchecked")
    public Act getById(Long id) throws GenericDAOException {
        logger.info("Find act, id: {}", id);
        Assert.notNull(id, "Id is null");

        DetachedCriteria criteria = DetachedCriteria.forClass(Act.class);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.isNull(DELETED));
        List<Act> foundActs = (List<Act>) hibernateTemplate.findByCriteria(criteria);
        return CollectionUtils.isNotEmpty(foundActs) ? foundActs.get(0) : null;
    }

    public List<Act> findActsByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse id: {}", firstResult, maxResults, warehouseId);
        Assert.notNull(warehouseId, ERROR_WAREHOUSE_ID_IS_NULL);

        String queryHql = "SELECT DISTINCT act" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND act.deleted IS NULL" +
                " ORDER BY act.date DESC";

        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.list();
    }

    public List<Act> findActsByCompanyId(Long warehouseCompanyId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find acts, first result: {}, max results: {}, warehouse company id: {}", firstResult, maxResults, warehouseCompanyId);
        Assert.notNull(warehouseCompanyId, ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);

        String queryHql = "SELECT DISTINCT act" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse " +
                " INNER JOIN WarehouseCompany company ON warehouse.warehouseCompany = company" +
                "  WHERE company.idWarehouseCompany = :warehouseCompanyId AND act.deleted IS NULL" +
                " ORDER BY act.date DESC";

        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.list();
    }


    public List<Act> findByGoodsId(Long goodsId) throws GenericDAOException {
        logger.info("Find acts for goods, goods id: {}", goodsId);
        Assert.notNull(goodsId, "Goods id is null");

        String queryHql = "SELECT DISTINCT act FROM Act act" +
                " INNER JOIN act.goods goods" +
                " WHERE goods.id = :goodsId AND act.deleted IS NULL" +
                " ORDER BY act.date DESC";

        Query<Act> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);

        return query.list();
    }

    public long getActsCountByWarehouse(Long warehouseId) throws GenericDAOException {
        logger.info("Get goods count, warehouse id: {}", warehouseId);
        Assert.notNull(warehouseId, ERROR_WAREHOUSE_ID_IS_NULL);

        String queryHql = "SELECT  count(DISTINCT act)" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND act.deleted IS NULL";

        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);

        return query.getSingleResult();
    }

    public long getActsCountByCompany(Long warehouseCompanyId) throws GenericDAOException {
        logger.info("Get goods count, warehouse company id: {}", warehouseCompanyId);
        Assert.notNull(warehouseCompanyId, ERROR_WAREHOUSE_COMPANY_ID_IS_NULL);

        String queryHql = "SELECT  count(DISTINCT act)" +
                " FROM Act act" +
                " INNER JOIN Warehouse warehouse ON warehouse = act.warehouse " +
                " INNER JOIN WarehouseCompany company ON warehouse.warehouseCompany = company" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND act.deleted IS NULL";

        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);

        return query.getSingleResult();
    }

    public long getActsSearchCount(DetachedCriteria criteria) throws GenericDAOException {
        logger.info("Get acts search result count");
        return ((List<Long>) hibernateTemplate.findByCriteria(criteria)).get(0);
    }

    public WarehouseCompany findWarehouseCompanyOfAct(Long actId) throws GenericDAOException {
        logger.info("Find warehouse company,act id: {}", actId);
        Assert.notNull(actId, "Act id is null");

        String queryHql = "SELECT  warehouseCompany" +
                " FROM WarehouseCompany warehouseCompany" +
                " INNER JOIN Warehouse warehouse ON warehouse.warehouseCompany = warehouseCompany" +
                " INNER JOIN Act act ON act.warehouse = warehouse" +
                " WHERE act.id = :actId AND act.deleted IS NULL";

        Query<WarehouseCompany> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("actId", actId);

        return query.getSingleResult();
    }

    public Warehouse findWarehouseOfAct(Long actId) throws GenericDAOException {
        logger.info("Find warehouse,act id: {}", actId);
        Assert.notNull(actId, "Act id is null");

        String queryHql = "SELECT  warehouse" +
                " FROM Warehouse warehouse" +
                " INNER JOIN Act act ON act.warehouse = warehouse" +
                " WHERE act.id = :actId AND act.deleted IS NULL";

        Query<Warehouse> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("actId", actId);

        return query.getSingleResult();
    }
}
