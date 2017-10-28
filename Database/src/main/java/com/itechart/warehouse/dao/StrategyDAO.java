package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Strategy;
import org.springframework.stereotype.Repository;

/**
 * Created by Lenovo on 14.10.2017.
 */
@Repository
public class StrategyDAO extends DAO<Strategy> {
    public StrategyDAO() {
        super(Strategy.class);
    }
}
