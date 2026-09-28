package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.TrainerJpaEntity;

public interface TrainerRepository extends BaseRepository<TrainerJpaEntity, UUID> {
  Optional<TrainerJpaEntity> findByUsername(String username);

  List<TrainerJpaEntity> findTrainersNotAssignedToTrainee(String traineeUsername);
}
