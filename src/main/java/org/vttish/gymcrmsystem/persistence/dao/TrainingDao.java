package org.vttish.gymcrmsystem.persistence.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymcrmsystem.entity.Training;

public interface TrainingDao {
  Training create(Training training);

  Optional<Training> selectById(UUID id);

  List<Training> selectAll();
}
