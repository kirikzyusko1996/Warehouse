package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.GoodsStatusName;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goodsIdList status name DAO.
 */
@Repository
public class GoodsStatusNameDAO extends DAO<GoodsStatusName> {
    public GoodsStatusNameDAO() {
        super(GoodsStatusName.class);
    }
}
