package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Goods;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goods DAO.
 */
@Repository
public class GoodsDAO extends DAO<Goods> {
    public GoodsDAO() {
        super(Goods.class);
    }

}
