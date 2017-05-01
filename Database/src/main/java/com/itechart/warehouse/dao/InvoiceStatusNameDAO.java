package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.InvoiceStatus;
import com.itechart.warehouse.entity.InvoiceStatusName;
import org.springframework.stereotype.Repository;


@Repository
public class InvoiceStatusNameDAO extends DAO<InvoiceStatusName>{
    public InvoiceStatusNameDAO() {
        super(InvoiceStatusName.class);
    }
}
