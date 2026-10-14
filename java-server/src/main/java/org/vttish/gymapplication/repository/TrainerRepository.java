package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.Trainer;

public interface TrainerRepository extends BaseRepository<Trainer, UUID> {
  Optional<Trainer> findByUsername(String username);

  List<Trainer> findAllByUsernameIn(List<String> usernames);

  List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername);
}
