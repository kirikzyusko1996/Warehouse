package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Role;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class RoleDAO extends DAO<Role> {
    public RoleDAO() {
        super(Role.class);
    }

    public List<Role> getRoles() throws GenericDAOException {
        logger.info("Get roles");

        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        return super.findAll(criteria, -1, -1);
    }

    public Role findRoleByName(String roleName) throws GenericDAOException {
        logger.info("Find role, name: {}", roleName);
        Assert.notNull(roleName, "Role name is null");

        String queryHql = "SELECT role FROM Role role WHERE role.name = :name";
        Query<Role> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", roleName);

        return query.getSingleResult();
    }
}
