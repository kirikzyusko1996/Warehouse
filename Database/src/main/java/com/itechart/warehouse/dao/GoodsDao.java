package com.itechart.warehouse.dao;

import entity.Goods;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goods DAO.
 */
@Repository
public class GoodsDao extends DAO<Goods> {
    public GoodsDao() {
        super(Goods.class);
    }
}
