package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.QuantityUnit;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implementation of quantity unit DAO.
 */
@Repository
public class QuantityUnitDAO extends DAO<QuantityUnit> {
    public QuantityUnitDAO() {
        super(QuantityUnit.class);
    }

    public List<QuantityUnit> getQuantityUnits() throws GenericDAOException {
        logger.info("Get quantity units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(QuantityUnit.class);
        return super.findAll(criteria, -1, -1);
    }

    public QuantityUnit findQuantityUnitByName(String unitName) throws GenericDAOException {
        logger.info("Find unit, name: {}", unitName);
        Assert.notNull(unitName, "Unit name is null");

        String queryHql = "SELECT quantityUnit FROM QuantityUnit quantityUnit WHERE quantityUnit.name = :name";
        Query<QuantityUnit> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", unitName);

        return query.getSingleResult();
    }

}
