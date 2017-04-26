package com.itechart.warehouse.dao;


import com.itechart.warehouse.entity.Act;
import org.springframework.stereotype.Repository;

/**
 * Implementation of act DAO.
 */
@Repository
public class ActDAO extends DAO<Act> {
    public ActDAO() {
        super(Act.class);
    }
}
