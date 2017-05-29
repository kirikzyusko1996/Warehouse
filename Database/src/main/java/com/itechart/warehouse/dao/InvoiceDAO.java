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
    }}
