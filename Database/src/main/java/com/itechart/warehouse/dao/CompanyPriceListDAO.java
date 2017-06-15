package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.CompanyPriceList;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 15.06.2017.
 */
@Repository
public class CompanyPriceListDAO extends DAO<CompanyPriceList> {
    public CompanyPriceListDAO() {
        super(CompanyPriceList.class);
    }
}
