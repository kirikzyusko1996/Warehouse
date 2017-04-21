package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.GoodsStatusName;
import org.springframework.stereotype.Repository;

/**
 * Implementation of goods status name DAO.
 */
@Repository
public class GoodsStatusNameDao extends DAO<GoodsStatusName> {
    public GoodsStatusNameDao() {
        super(GoodsStatusName.class);
    }
}