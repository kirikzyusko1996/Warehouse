package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Invoice;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class InvoiceDAO extends DAO<Invoice>{
    public InvoiceDAO() {
        super(Invoice.class);
    }

    public List<Invoice> findInvoicesByWarehouseId(Long warehouseId, int page, int count)
            throws GenericDAOException{
        String queryHql = "SELECT DISTINCT invoice" +
                " FROM Invoice invoice" +
                " INNER JOIN Warehouse warehouse ON warehouse = invoice.warehouse" +
                " WHERE warehouse.idWarehouse = :warehouseId";
        Query<Invoice> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseId", warehouseId);
        query.setFirstResult(page);
        query.setMaxResults(count);
        return query.list();
    }
}
