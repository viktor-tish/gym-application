package org.vttish.gymapplication.persistence.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T, I> {
  T create(T entity);

  Optional<T> selectById(I id);

  List<T> selectAll();

  boolean existsById(I id);

  void delete(I id);
}
