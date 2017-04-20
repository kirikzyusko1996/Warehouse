package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.User;

/**
 * Created by Lenovo on 20.04.2017.
 */
public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }
}
