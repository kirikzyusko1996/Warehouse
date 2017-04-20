package com.itechart.warehouse.dao;

import entity.ActType;
import org.springframework.stereotype.Repository;

/**
 * Implementation of act type DAO.
 */
@Repository
public class ActTypeDao extends DAO<ActType> {
    public ActTypeDao() {
        super(ActType.class);
    }
}
