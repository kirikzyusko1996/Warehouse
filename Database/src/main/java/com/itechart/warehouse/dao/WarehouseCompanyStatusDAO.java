package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCompanyStatus;
import org.springframework.stereotype.Repository;


@Repository
public class WarehouseCompanyStatusDAO extends DAO<WarehouseCompanyStatus> {
    public WarehouseCompanyStatusDAO() {
        super(WarehouseCompanyStatus.class);
    }
}