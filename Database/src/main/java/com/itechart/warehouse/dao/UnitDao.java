package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Unit;
import org.springframework.stereotype.Repository;

/**
 * Implementation of unit DAO.
 */
@Repository
public class UnitDao extends DAO<Unit> {
    public UnitDao() {
        super(Unit.class);
    }
}
