package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.TransportCompany;
import org.springframework.stereotype.Repository;

@Repository
public class TransportCompanyDAO extends DAO<TransportCompany> {

    public TransportCompanyDAO() {
        super(TransportCompany.class);
    }
}
