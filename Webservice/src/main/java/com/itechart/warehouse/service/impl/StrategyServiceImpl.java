package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.StrategyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Strategy;
import com.itechart.warehouse.service.services.StrategyService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by Lenovo on 14.10.2017.
 */
@Service
public class StrategyServiceImpl implements StrategyService {
    private StrategyDAO strategyDAO;
    private GoodsDAO goodsDAO;

    @Autowired
    public void setStrategyDAO(StrategyDAO strategyDAO) {
        this.strategyDAO = strategyDAO;
    }

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Transactional(readOnly = true)
    public List<Goods> getListForLearning() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Goods.class);
        criteria.add(Restrictions.isNotNull("strategy.idStrategy"));
        List<Goods> goodsList = goodsDAO.findAll(criteria, -1, -1);
        return goodsList;
    }

    @Transactional(readOnly = true)
    public int getQuantityStrategies() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Strategy.class);
        return strategyDAO.findAll(criteria, -1, -1).size();
    }
}
