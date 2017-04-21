package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.GoodsStatus;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goods status DAO.
 */
@Repository
public class GoodsStatusDao extends DAO<GoodsStatus> {
    public GoodsStatusDao() {
        super(GoodsStatus.class);
    }
}
