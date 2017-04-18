package dao;

import entity.TransportCompany;
import org.hibernate.Session;

public class TransportCompanyDAO extends DAO<TransportCompany> {

    public TransportCompanyDAO(Session session) {
        super(TransportCompany.class, session);
    }
}
