package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.StorageCell;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageCellDAO extends DAO<StorageCell> {
    public StorageCellDAO() {
        super(StorageCell.class);
    }
}
