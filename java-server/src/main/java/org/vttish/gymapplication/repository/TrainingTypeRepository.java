package org.vttish.gymapplication.repository;

import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.TrainingType;

public interface TrainingTypeRepository extends BaseRepository<TrainingType, UUID> {
  Optional<TrainingType> findByName(String name);
}
