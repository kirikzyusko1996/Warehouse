package com.itechart.warehouse.controller;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.forecasting.Category;
import com.itechart.warehouse.dto.forecasting.StrategyDTO;
import com.itechart.warehouse.entity.Strategy;
import com.itechart.warehouse.service.forecasting.ForecastingService;
import com.itechart.warehouse.service.forecasting.ForecastingServiceImpl;
import com.itechart.warehouse.service.services.StrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by Lenovo on 07.10.2017.
 */
@RestController
@RequestMapping(value = "/forecasting")
@Validated
public class ForecastingController {
    private Logger logger = LoggerFactory.getLogger(ForecastingController.class);
    private ForecastingService forecastingService;
    private StrategyService strategyService;

    @Autowired
    public void setForecastingServiceImpl(ForecastingService forecastingService) {
        this.forecastingService = forecastingService;
    }

    @Autowired
    public void setStrategyService(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @RequestMapping(value = "/strategy", method = RequestMethod.GET)
    public ResponseEntity<Strategy> getStrategyByNeuralNetwork(@RequestParam(defaultValue = "-1") long idGoods){
        logger.info("Request for getting strategy.");

        Strategy strategy = forecastingService.getStrategyByGoods(idGoods);
        try {
            strategyService.getListForLearning();
        } catch (GenericDAOException e) {
            logger.error(e.getMessage());
        }
        /*StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setCategory(Category.RARELY);
        strategyDTO.setDaysKeeping(15);
        strategyDTO.setDaysUntilRelease(21);
        strategyDTO.setPrice(12);
        strategyDTO.setStrategy(Strategy.DISCOUNT);*/

        return new ResponseEntity<>(strategy, HttpStatus.OK);
    }
}
