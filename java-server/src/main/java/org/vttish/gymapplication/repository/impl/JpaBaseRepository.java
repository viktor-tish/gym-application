package org.vttish.gymapplication.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.vttish.gymapplication.entity.BaseEntity;
import org.vttish.gymapplication.repository.BaseRepository;

@RequiredArgsConstructor
public abstract class JpaBaseRepository<T extends BaseEntity>
    implements BaseRepository<T, UUID> {

  @PersistenceContext
  protected EntityManager entityManager;

  protected final Class<T> entityClass;

  @Override
  public T save(T entity) {
    if (entity.getId() == null) {
      entityManager.persist(entity);
    } else {
      entity = entityManager.merge(entity);
    }

    return entity;
  }

  @Override
  public Optional<T> findById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }

    T entity = entityManager.find(entityClass, id);
    return Optional.ofNullable(entity);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<T> findAll() {
    String query = String.format("SELECT e FROM %s e", entityClass.getSimpleName());
    return entityManager.createQuery(query, entityClass).getResultList();
  }

  @SuppressWarnings("java:S2077")
  @Override
  public boolean existsById(UUID id) {
    if (id == null) {
      return false;
    }

    String query = String.format(
        "SELECT COUNT(e) FROM %s e WHERE e.id = :id",
        entityClass.getSimpleName()
    );

    Long count = entityManager.createQuery(query, Long.class)
        .setParameter("id", id)
        .getSingleResult();

    return count > 0;
  }

  @Override
  public void delete(UUID id) {
    findById(id).ifPresent(entity -> entityManager.remove(entity));
  }
}
