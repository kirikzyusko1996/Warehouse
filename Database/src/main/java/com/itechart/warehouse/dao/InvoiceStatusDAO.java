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
}
