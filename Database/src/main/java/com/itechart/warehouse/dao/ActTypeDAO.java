package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.ActType;
import org.springframework.stereotype.Repository;

/**
 * Implementation of act type DAO.
 */
@Repository
public class ActTypeDAO extends DAO<ActType> {
    public ActTypeDAO() {
        super(ActType.class);
    }
}
