package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class InvoiceStatusDAO extends DAO<InvoiceStatus>{
    public InvoiceStatusDAO() {
        super(InvoiceStatus.class);
    }

    public InvoiceStatus findStatusForInvoice(Long invoiceId)
            throws GenericDAOException {
        logger.info("Find status for invoice with  id: {}", invoiceId);
        String queryHql = "SELECT DISTINCT status" +
                " FROM InvoiceStatus status" +
                " INNER JOIN Invoice invoice ON invoice = status.invoice" +
                " WHERE invoice.id = :id";
        Query<InvoiceStatus> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("id", invoiceId);
        return query.getSingleResult();
    }

    public List<InvoiceStatus> findStatusesByWarehouseId(Long warehouseId, int page, int count)
            throws GenericDAOException {
        String queryHql = "SELECT DISTINCT status" +
                " FROM InvoiceStatus status" +
                " INNER JOIN Warehouse warehouse ON warehouse = status.invoice.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId";
        Query<InvoiceStatus> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(page);
        query.setMaxResults(count);
        return query.list();
    }
}
