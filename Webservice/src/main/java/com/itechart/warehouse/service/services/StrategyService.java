package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;

import java.util.List;

/**
 * Created by Lenovo on 14.10.2017.
 */
public interface StrategyService {
    List<Goods> getListForLearning() throws GenericDAOException;
    int getQuantityStrategies() throws GenericDAOException;
}
