package dao;

import entity.Driver;
import entity.PriceList;

/**
 * Created by Alexey on 19.04.2017.
 */
public class PriceListDAO extends DAO<PriceList> {
    public PriceListDAO() {
        super(PriceList.class);
    }
}
