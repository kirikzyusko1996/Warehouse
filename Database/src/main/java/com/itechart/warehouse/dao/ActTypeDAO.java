package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.ActType;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implementation of act type DAO.
 */
@Repository
public class ActTypeDAO extends DAO<ActType> {
    public ActTypeDAO() {
        super(ActType.class);
    }

    public List<ActType> getActTypes() throws GenericDAOException {
        logger.info("Get act types");
        DetachedCriteria criteria = DetachedCriteria.forClass(ActType.class);
        return super.findAll(criteria, -1, -1);
    }

    public ActType findActTypeByName(String name) throws GenericDAOException {
        logger.info("Find act type, name: {}", name);
        Assert.notNull(name, "Act type is null");

        String queryHql = "SELECT actType FROM ActType actType WHERE actType.name = :name";
        Query<ActType> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", name);

        return query.getSingleResult();
    }
}
