package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.StorageSpaceType;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageSpaceTypeDAO extends DAO<StorageSpaceType> {
    public StorageSpaceTypeDAO() {
        super(StorageSpaceType.class);
    }

    public List<StorageSpaceType> getStorageSpaceTypes() throws GenericDAOException {
        logger.info("Get storage space types list");
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        return super.findAll(criteria, -1, -1);
    }

    public StorageSpaceType findStorageTypeByName(String spaceTypeName) {
        logger.info("Find storage type, name: {}", spaceTypeName);
        Assert.notNull(spaceTypeName, "Storage type name is null");

        String queryHql = "SELECT storageType FROM StorageSpaceType storageType WHERE storageType.name = :name";
        Query<StorageSpaceType> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("name", spaceTypeName);

        return query.getSingleResult();
    }

}
