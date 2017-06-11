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
}
