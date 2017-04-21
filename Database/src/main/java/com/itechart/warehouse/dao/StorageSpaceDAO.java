package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.StorageSpace;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexey on 19.04.2017.
 */
@Repository
public class StorageSpaceDAO  extends DAO<StorageSpace>{
    public StorageSpaceDAO() {
        super(StorageSpace.class);
    }
}
