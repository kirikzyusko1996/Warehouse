package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.Invoice;
import org.springframework.stereotype.Repository;


@Repository
public class InvoiceDAO extends DAO<Invoice>{
    public InvoiceDAO(Class<Invoice> entityClass) {
        super(entityClass);
    }
}
