package dao;

import model.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * A generic abstract DAO that connects to the default
 * persistence context and implements basic CRUD operations
 */
public abstract class BaseDao<E extends BaseEntity>  {

    @PersistenceContext(unitName = "default")
    protected EntityManager entityManager;

    private final Class<E> type;

    public BaseDao(Class<E> type) {
        this.type = type;
    }

    public List<E> all() {
        return entityManager.createQuery("from " + type.getSimpleName(), type).getResultList();
    }

    public E findById(Long id) {
        E entity = entityManager.find(type, id);
        if (entity == null)
            throw new EntityNotFoundException();
        return entity;
    }

    @Transactional
    public void save(E entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void update(E updatedEntity, Long id) {
        E old = entityManager.find(type, id);
        if (old == null)
            throw new EntityNotFoundException();
        old.copy(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        entityManager.remove(entityManager.find(type, id));
    }
}