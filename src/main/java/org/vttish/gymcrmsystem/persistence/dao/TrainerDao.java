package org.vttish.gymcrmsystem.persistence.dao;

import org.vttish.gymcrmsystem.entity.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerDao {
    Trainer create(Trainer trainer);
    Optional<Trainer> selectById(UUID id);
    List<Trainer> selectAll();

    boolean existsByUsername(String username);
}
