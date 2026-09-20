package org.vttish.gymcrmsystem.persistence.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymcrmsystem.entity.Trainee;

public interface TraineeDao {
  Trainee create(Trainee trainee);

  Optional<Trainee> selectById(UUID id);

  List<Trainee> selectAll();

  void delete(UUID id);

  boolean existsById(UUID id);

  boolean existsByUsername(String username);
}
