package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.PriceUnit;
import org.springframework.stereotype.Repository;

/**
 * Implementation of price unit DAO.
 */
@Repository
public class PriceUnitDAO extends DAO<PriceUnit> {
    public PriceUnitDAO() {
        super(PriceUnit.class);
    }
}
