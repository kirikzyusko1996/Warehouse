package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.GoodsStatusName;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implementation of goodsIdList status name DAO.
 */
@Repository
public class GoodsStatusNameDAO extends DAO<GoodsStatusName> {
    public GoodsStatusNameDAO() {
        super(GoodsStatusName.class);
    }

    public List<GoodsStatusName> getStatusNames() throws GenericDAOException {
        logger.info("Get status names list");

        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        return super.findAll(criteria, -1, -1);
    }

    public GoodsStatusName findGoodsStatusNameByName(String goodsStatusNameName) {
        logger.info("Find goods status name object, name: {}", goodsStatusNameName);
        Assert.notNull(goodsStatusNameName, "Goods status name is null");


        String queryHql = "SELECT statusName FROM GoodsStatusName statusName WHERE statusName.name = :name";

        Query<GoodsStatusName> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", goodsStatusNameName);

        return query.getSingleResult();

    }
}
