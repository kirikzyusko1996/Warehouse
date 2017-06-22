package com.itechart.warehouse.dao;

import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.TransportCompany;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public TransportCompany findByIdBeforeUpdate(Long id) throws GenericDAOException {
        Optional<TransportCompany> optional = findById(id);
        if (optional.isPresent()) {
            TransportCompany company = optional.get();
            hibernateTemplate.getSessionFactory().getCurrentSession().clear();
            return company;
        } else {
            throw new GenericDAOException("Transport company not found");
        }
    }
}
