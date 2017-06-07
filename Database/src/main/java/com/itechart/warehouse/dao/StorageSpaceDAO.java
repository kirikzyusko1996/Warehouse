package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageSpaceDAO  extends DAO<StorageSpace>{
    public StorageSpaceDAO() {
        super(StorageSpace.class);
    }

    public WarehouseCompany findWarehouseCompanyBySpace(Long id_space) throws GenericDAOException {
        logger.info("Find warehouse company of cell with id: {}", id_space);
        String queryHql = "SELECT warehouseCompany FROM WarehouseCompany warehouseCompany" +
                " INNER JOIN Warehouse warehouse ON warehouse.warehouseCompany=warehouseCompany" +
                " INNER JOIN StorageSpace storageSpace ON storageSpace.warehouse=warehouse" +
                " WHERE storageSpace.idStorageSpace = :storageSpaceId";
        Query<WarehouseCompany> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("storageSpaceId", id_space);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            logger.info("Entity not found!");
            return null;
        }
    }
}
