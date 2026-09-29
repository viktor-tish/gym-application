package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, I> {
  T save(T entity);

  Optional<T> findById(I id);

  List<T> findAll();

  boolean existsById(I id);

  void delete(I id);
}
