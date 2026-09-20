package org.vttish.gymcrmsystem.persistence.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymcrmsystem.entity.Trainer;

public interface TrainerDao {
  Trainer create(Trainer trainer);

  Optional<Trainer> selectById(UUID id);

  List<Trainer> selectAll();

  boolean existsById(UUID id);

  boolean existsByUsername(String username);
}
