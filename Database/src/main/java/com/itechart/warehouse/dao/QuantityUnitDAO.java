package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.QuantityUnit;
import org.springframework.stereotype.Repository;

/**
 * Implementation of quantity unit DAO.
 */
@Repository
public class QuantityUnitDAO extends DAO<QuantityUnit> {
    public QuantityUnitDAO() {
        super(QuantityUnit.class);
    }
}
