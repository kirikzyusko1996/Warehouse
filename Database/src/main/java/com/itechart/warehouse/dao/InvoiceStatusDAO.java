package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


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
}
