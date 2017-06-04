package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageCellDAO extends DAO<StorageCell> {
    public StorageCellDAO() {
        super(StorageCell.class);
    }

    public WarehouseCompany findWarehouseCompanyByCell(Long id_cell) throws GenericDAOException {
        logger.info("Find warehouse company of cell with id: {}", id_cell);
        String queryHql = "SELECT warehouseCompany FROM WarehouseCompany warehouseCompany" +
                " INNER JOIN Warehouse warehouse ON warehouse.warehouseCompany=warehouseCompany" +
                " INNER JOIN StorageSpace storageSpace ON storageSpace.warehouse=warehouse" +
                " INNER JOIN StorageCell storageCell ON storageCell.storageSpace=storageSpace" +
                " WHERE storageCell.idStorageCell = :storageCellId";
        Query<WarehouseCompany> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("storageCellId", id_cell);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
}
