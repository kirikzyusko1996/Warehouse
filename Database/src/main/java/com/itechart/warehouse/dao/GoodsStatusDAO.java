package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.GoodsStatus;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goodsList status DAO.
 */
@Repository
public class GoodsStatusDAO extends DAO<GoodsStatus> {
    public GoodsStatusDAO() {
        super(GoodsStatus.class);
    }
}
