package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.User;
import org.springframework.stereotype.Repository;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }
}
