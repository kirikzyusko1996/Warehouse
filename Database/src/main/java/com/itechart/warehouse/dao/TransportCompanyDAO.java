package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.TransportCompany;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransportCompanyDAO extends DAO<TransportCompany> {

    public TransportCompanyDAO() {
        super(TransportCompany.class);
    }

    public List<TransportCompany> findCustomersByWarehouseCompanyId(Long warehouseCompanyId, int page, int count)
            throws GenericDAOException {
        logger.info("Find {} transport companies starting from {} by warehouse company id: {}", page, count, warehouseCompanyId);
        String queryHql = "SELECT DISTINCT transport" +
                " FROM TransportCompany transport" +
                " INNER JOIN WarehouseCompany company ON company = transport.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId";
        Query<TransportCompany> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(page);
        query.setMaxResults(count);
        return query.list();
    }
}
