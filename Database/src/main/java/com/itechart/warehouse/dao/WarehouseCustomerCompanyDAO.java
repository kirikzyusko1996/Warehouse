package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import org.springframework.stereotype.Repository;


@Repository
public class WarehouseCustomerCompanyDAO extends DAO<WarehouseCustomerCompany> {
    public WarehouseCustomerCompanyDAO() {
        super(WarehouseCustomerCompany.class);
    }
}
