package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Unit;
import org.springframework.stereotype.Repository;

/**
 * Implementation of unit DAO.
 */
@Repository
public class UnitDAO extends DAO<Unit> {
    public UnitDAO() {
        super(Unit.class);
    }
}
