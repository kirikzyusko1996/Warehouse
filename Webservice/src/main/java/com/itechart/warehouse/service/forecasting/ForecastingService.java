package com.itechart.warehouse.service.forecasting;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Strategy;

import java.io.IOException;

/**
 * Created by Lenovo on 07.10.2017.
 */
public interface ForecastingService {
    Strategy getStrategyByGoods(Long idGoods);
    void train() throws GenericDAOException, IOException, InterruptedException;
}
