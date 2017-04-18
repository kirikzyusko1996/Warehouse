package dao;

import exceptions.GenericDAOException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class DAO<T> {
    private final Class<T> entityClass;
    private Session session;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DAO(Class<T> entityClass, Session session) {
        this.entityClass = entityClass;
        this.session = session;
    }

    public List<T> findAll(int firstResult, int maxResults) throws GenericDAOException {
        return (List<T>) session.createCriteria(entityClass).setFirstResult(firstResult).setMaxResults(maxResults);
    }

    public T findById(Long id) throws GenericDAOException {
        return id != null ? (T) session.get(entityClass, id) : null;
    }

    T insert(T entity) throws GenericDAOException {
        if (entity == null) return null;
        try {
            session.save(entity);
            return entity;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GenericDAOException(e);
        }
    }

    T update(T entity) throws GenericDAOException {
        try {
            if (entity != null) {
                session.update(entity);
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
            session.delete(entity);
            session.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GenericDAOException(e);
        }
    }

}
