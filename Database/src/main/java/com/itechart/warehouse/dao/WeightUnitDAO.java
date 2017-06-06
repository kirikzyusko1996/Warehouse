package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.WeightUnit;
import org.springframework.stereotype.Repository;

/**
 * Implementation of weight unit DAO.
 */
@Repository
public class WeightUnitDAO extends DAO<WeightUnit> {
    public WeightUnitDAO() {
        super(WeightUnit.class);
    }
}
