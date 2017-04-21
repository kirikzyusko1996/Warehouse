package com.itechart.warehouse.dao;

import com.itechart.warehouse.exceptions.GenericDAOException;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.util.List;
import java.util.Optional;

public abstract class DAO<T> {
    private final Class<T> entityClass;
    protected HibernateTemplate hibernateTemplate;
    private final Logger logger;

    public DAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.logger = LoggerFactory.getLogger(entityClass);
    }

    @Autowired
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public boolean isExistsEntity(Long id) throws GenericDAOException {
        Optional<? extends T> resultFind = findById(id);
        resultFind.ifPresent(hibernateTemplate::evict);
        return resultFind.isPresent();
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll(DetachedCriteria criteria, int firstResult, int maxResults) throws GenericDAOException {
        return (List<T>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    public Optional<T> findById(Long id) throws GenericDAOException {
        return id != null ? Optional.ofNullable(hibernateTemplate.get(entityClass, id)) : Optional.empty();
    }

    public T insert(T entity) throws GenericDAOException {
        if (entity == null) return null;
        try {
            hibernateTemplate.save(entity);
            return entity;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GenericDAOException(e);
        }
    }

    public T update(T entity) throws GenericDAOException {
        try {
            if (entity != null) {
                hibernateTemplate.update(entity);
                return entity;
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GenericDAOException(e);
        }
    }

    public void delete(T entity) throws GenericDAOException {
        if (entity == null) return;
        try {
            hibernateTemplate.delete(entity);
            hibernateTemplate.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GenericDAOException(e);
        }
    }

}
