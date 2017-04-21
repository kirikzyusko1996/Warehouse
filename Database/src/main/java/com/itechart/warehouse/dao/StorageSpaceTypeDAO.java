package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.StorageSpaceType;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageSpaceTypeDAO extends DAO<StorageSpaceType>{
    public StorageSpaceTypeDAO() {
        super(StorageSpaceType.class);
    }
}
