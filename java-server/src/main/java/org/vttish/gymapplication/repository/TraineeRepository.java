package org.vttish.gymapplication.repository;

import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.TraineeJpaEntity;

public interface TraineeRepository extends BaseRepository<TraineeJpaEntity, UUID> {
  Optional<TraineeJpaEntity> findByUsername(String username);

  void deleteByUsername(String username);
}
