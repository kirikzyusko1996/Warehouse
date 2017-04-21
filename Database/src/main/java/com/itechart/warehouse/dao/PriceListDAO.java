package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.PriceList;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class PriceListDAO extends DAO<PriceList> {
    public PriceListDAO() {
        super(PriceList.class);
    }
}
