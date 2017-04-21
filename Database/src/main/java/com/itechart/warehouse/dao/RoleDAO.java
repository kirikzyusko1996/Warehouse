package com.itechart.warehouse.dao;

import com.itechart.warehouse.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * Created by Lenovo on 20.04.2017.
 */
@Repository
public class RoleDAO extends DAO<Role> {
    public RoleDAO() {
        super(Role.class);
    }
}
