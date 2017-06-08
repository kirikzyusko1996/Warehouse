package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.User;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
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
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND user.deleted IS NULL" +
                " ORDER BY user.id";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public long getUsersCount(Long warehouseCompanyId) throws GenericDAOException{
        logger.info("Get users count for warehouse company with id: {}", warehouseCompanyId);
        String queryHql = "SELECT count(DISTINCT user.id)" +
                " FROM User user" +
                " INNER JOIN WarehouseCompany company ON company = user.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND user.deleted IS NULL";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        return query.getSingleResult();
    }

    public List<User> findUsersByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find {} users starting from {} by warehouse id: {}", maxResults, firstResult, warehouseId);
        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " INNER JOIN Warehouse warehouse ON warehouse = user.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND user.deleted IS NULL" +
                " ORDER BY user.id";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }


    public List<User> findUsersByBirthDay(DateTime date) throws GenericDAOException {
        logger.info("Find users by birthday: {}", date);
        String queryHql = "SELECT user" +
                " FROM User user" +
                " WHERE MONTH(user.dateOfBirth) = :month AND DAY(user.dateOfBirth) = :day AND user.deleted IS NULL" +
                " ORDER BY user.id";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("month", date.getMonthOfYear());
        query.setParameter("day", date.getDayOfMonth());
        return query.list();
    }


    public User findUserById(Long id) throws GenericDAOException {
        logger.info("Find user by id: {}", id);
        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " WHERE user.id = :id AND user.deleted IS NULL";
        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            throw new GenericDAOException(e);
        }
    }
}
