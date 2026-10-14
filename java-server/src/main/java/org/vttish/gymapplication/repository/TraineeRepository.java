package org.vttish.gymapplication.repository;

import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.Trainee;

public interface TraineeRepository extends BaseRepository<Trainee, UUID> {
  boolean existsByUsername(String username);

  Optional<Trainee> findByUsername(String username);

  void deleteByUsername(String username);
}
