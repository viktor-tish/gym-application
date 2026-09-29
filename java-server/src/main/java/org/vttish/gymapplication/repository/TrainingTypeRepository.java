package org.vttish.gymapplication.repository;

import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.TrainingTypeJpaEntity;

public interface TrainingTypeRepository extends BaseRepository<TrainingTypeJpaEntity, UUID> {
  Optional<TrainingTypeJpaEntity> findByName(String name);
}
