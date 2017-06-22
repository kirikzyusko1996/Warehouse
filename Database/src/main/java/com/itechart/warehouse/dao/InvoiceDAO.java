package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Invoice;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public class InvoiceDAO extends DAO<Invoice> {
    public InvoiceDAO() {
        super(Invoice.class);
    }

    public List<Invoice> findInvoicesByWarehouseIdAndStatus(Long companyId, String status, int page, int count)
            throws GenericDAOException {
        String queryHql = "SELECT DISTINCT invoice" +
                " FROM Invoice invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId " +
                " AND invoice.currentStatus.statusName.name = :status";
        Query<Invoice> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", companyId);
        query.setParameter("status", status);
        query.setFirstResult(page);
        query.setMaxResults(count);
        return query.list();
    }

    public Long findInvoicesCountByWarehouseIdAndStatusForController(Long companyId) throws GenericDAOException {
        String queryHql = "SELECT COUNT(DISTINCT invoice)" +
                " FROM Invoice invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId " +
                " AND (invoice.currentStatus.statusName.name = :status1" +
                " OR invoice.currentStatus.statusName.name = :status2)";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", companyId);
        query.setParameter("status1", "REGISTERED_OUTGOING");
        query.setParameter("status2", "REGISTERED_INCOMING");
        return query.getSingleResult();
    }

    public Long findInvoicesCountByWarehouseIdAndStatus(Long companyId, String status) throws GenericDAOException {
        String queryHql = "SELECT COUNT(DISTINCT invoice)" +
                " FROM Invoice invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId " +
                " AND invoice.currentStatus.statusName.name = :status";
        Query<Long> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", companyId);
        query.setParameter("status", status);
        return query.getSingleResult();
    }
}
