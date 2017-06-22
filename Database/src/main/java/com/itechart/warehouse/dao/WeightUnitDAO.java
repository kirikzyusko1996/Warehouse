package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.WeightUnit;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implementation of weight unit DAO.
 */
@Repository
public class WeightUnitDAO extends DAO<WeightUnit> {
    public WeightUnitDAO() {
        super(WeightUnit.class);
    }

    public List<WeightUnit> getWeightUnits() throws GenericDAOException {
        logger.info("Get weight units list");
        DetachedCriteria criteria = DetachedCriteria.forClass(WeightUnit.class);
        return super.findAll(criteria, -1, -1);
    }

    public WeightUnit findWeightUnitByName(String unitName)throws GenericDAOException  {
        logger.info("Find unit, name: {}", unitName);
        Assert.notNull(unitName, "Unit name is null");

        String queryHql = "SELECT weightUnit FROM WeightUnit weightUnit WHERE weightUnit.name = :name";
        Query<WeightUnit> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", unitName);

        return query.getSingleResult();
    }
}
