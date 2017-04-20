package dao;

import entity.Driver;
import org.hibernate.Session;

public class DriverDAO extends DAO<Driver> {

    public DriverDAO() {
        super(Driver.class);
    }
}
