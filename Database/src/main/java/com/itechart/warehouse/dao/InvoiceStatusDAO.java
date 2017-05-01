package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import org.springframework.stereotype.Repository;


@Repository
public class InvoiceStatusDAO extends DAO<InvoiceStatus>{
    public InvoiceStatusDAO() {
        super(InvoiceStatus.class);
    }
}
