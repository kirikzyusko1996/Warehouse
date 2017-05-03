package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.User;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }

    public List<User> findUsersByWarehouseCompanyId(Long warehouseCompanyId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find {} users starting from {} by warehouse company id: {}", maxResults, firstResult, warehouseCompanyId);
        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " INNER JOIN WarehouseCompany company ON company = user.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public User findUserById(Long id) throws GenericDAOException {
        logger.info("Find user by id: {}", id);
        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " WHERE user.id = :id";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
}
