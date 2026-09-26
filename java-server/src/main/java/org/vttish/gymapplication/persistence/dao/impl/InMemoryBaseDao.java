package org.vttish.gymapplication.persistence.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Setter;
import org.springframework.util.Assert;
import org.vttish.gymapplication.persistence.dao.BaseDao;

@Setter
public abstract class InMemoryBaseDao<T, I> implements BaseDao<T, I> {
  private Map<I, T> storage;

  public abstract I getEntityId(T entity);

  @Override
  public T create(T entity) {
    Assert.notNull(entity, "Entity must not be null");
    I id = getEntityId(entity);
    Assert.notNull(id, "Entity ID must not be null");
    Assert.isTrue(!storage.containsKey(id), "Entity already exists with such ID");

    storage.put(id, entity);
    return entity;
  }

  @Override
  public Optional<T> selectById(I id) {
    Assert.notNull(id, "ID must not be null");
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<T> selectAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public boolean existsById(I id) {
    Assert.notNull(id, "ID must not be null");
    return storage.containsKey(id);
  }

  @Override
  public void delete(I id) {
    Assert.notNull(id, "ID must not be null");
    storage.remove(id);
  }
}
