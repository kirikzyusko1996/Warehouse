package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.WarehouseCompany;
import org.springframework.stereotype.Repository;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class WarehouseCompanyDAO extends DAO<WarehouseCompany> {
    public WarehouseCompanyDAO() {
        super(WarehouseCompany.class);
    }
}