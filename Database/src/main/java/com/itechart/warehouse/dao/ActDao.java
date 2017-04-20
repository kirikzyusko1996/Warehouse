package com.itechart.warehouse.dao;

import entity.Act;
import org.springframework.stereotype.Repository;

/**
 * Implementation of act DAO.
 */
@Repository
public class ActDao extends DAO<Act> {
    public ActDao() {
        super(Act.class);
    }
}
