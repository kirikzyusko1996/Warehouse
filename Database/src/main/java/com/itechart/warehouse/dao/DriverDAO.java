package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Driver;
import org.springframework.stereotype.Repository;

@Repository
public class DriverDAO extends DAO<Driver> {

    public DriverDAO() {
        super(Driver.class);
    }
}
