package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class WarehouseCustomerCompanyDAO extends DAO<WarehouseCustomerCompany> {
    public WarehouseCustomerCompanyDAO() {
        super(WarehouseCustomerCompany.class);
    }

    public List<WarehouseCustomerCompany> findCustomersByWarehouseCompanyId(Long warehouseCompanyId, int firstResult, int maxResults)
            throws GenericDAOException {
        logger.info("Find {} customers starting from {} by warehouse company id: {}", maxResults, firstResult, warehouseCompanyId);
        String queryHql = "SELECT DISTINCT customer" +
                " FROM WarehouseCustomerCompany customer" +
                " INNER JOIN WarehouseCompany company ON company = customer.warehouseCompany" +
                " WHERE company.idWarehouseCompany = :warehouseCompanyId";
        Query<WarehouseCustomerCompany> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("warehouseCompanyId", warehouseCompanyId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }
}
