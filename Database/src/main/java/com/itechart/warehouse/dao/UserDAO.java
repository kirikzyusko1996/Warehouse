package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.User;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }


    public void setSettingsPreset(Long userId, Long presetId) throws GenericDAOException {
        logger.info("Set preset, user id: {}, preset id: {}", userId, presetId);
        Assert.notNull(userId == null, "User id is null");
        Assert.notNull(presetId == null, "Preset id is null");

        String queryHql = "UPDATE User user " +
                " SET user.presetId = :presetId" +
                " WHERE user.id = :userId";

        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("userId", userId);
        query.setParameter("presetId", presetId);
        query.executeUpdate();
    }

    public User findUserByLogin(String login) throws GenericDAOException {
        logger.info("Find user, login: {}", login);
        Assert.notNull(login, "Login is null");

        String queryHql = "SELECT user" +
                " FROM User user" +
                " WHERE user.login = :login AND user.deleted IS NULL";

        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("login", login);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> findUsersByWarehouseCompanyId(Long warehouseCompanyId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find users, first result: {}, max results: {}, warehouse company id: {}", firstResult, maxResults, warehouseCompanyId);
        Assert.notNull(warehouseCompanyId, "Warehouse company id is null");

        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " INNER JOIN WarehouseCompany company ON company = user.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND user.deleted IS NULL" +
                " ORDER BY user.id";

        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);

        if (firstResult != -1 && maxResults != -1) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    public long getUsersCount(Long warehouseCompanyId) throws GenericDAOException {
        logger.info("Get users count,warehouse company id: {}", warehouseCompanyId);
        Assert.notNull(warehouseCompanyId, "Warehouse company id is null");

        String queryHql = "SELECT count(DISTINCT user.id)" +
                " FROM User user" +
                " INNER JOIN WarehouseCompany company ON company = user.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId AND user.deleted IS NULL";

        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);

        return query.getSingleResult();
    }

    public List<User> findUsersByWarehouseId(Long warehouseId, int firstResult, int maxResults) throws GenericDAOException {
        logger.info("Find users, first result: {}, max results: {}, warehouse id: {}", firstResult, maxResults, warehouseId);
        Assert.notNull(warehouseId, "Warehouse id is null");

        String queryHql = "SELECT DISTINCT user" +
                " FROM User user" +
                " INNER JOIN Warehouse warehouse ON warehouse = user.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND user.deleted IS NULL" +
                " ORDER BY user.id";

        Query<User> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        if (firstResult != -1 && maxResults != -1) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    public long getUsersCountByWarehouseId(Long warehouseId) throws GenericDAOException {
        logger.info("Get users count,warehouse id: {}", warehouseId);
        Assert.notNull(warehouseId, "Warehouse id is null");

        String queryHql = "SELECT count(DISTINCT user.id)" +
                " FROM User user" +
                " INNER JOIN Warehouse warehouse ON warehouse = user.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId AND user.deleted IS NULL";

        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);

        return query.getSingleResult();
    }


    public List<User> findUsersByBirthDay(DateTime date) throws GenericDAOException {
        logger.info("Find users with birthday, date: {}", date);
        Assert.notNull(date, "Date is null");

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
        logger.info("Find user, id: {}", id);
        Assert.notNull(id, "Id is null");

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
