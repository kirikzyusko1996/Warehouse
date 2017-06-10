package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Warehouse;
import org.springframework.stereotype.Repository;

/**
 * DAO layer for warehouses
 * Created by Lenovo on 20.04.2017.
 */

@Repository
public class WarehouseDAO extends DAO<Warehouse> {
    public WarehouseDAO() {
        super(Warehouse.class);
    }
}
