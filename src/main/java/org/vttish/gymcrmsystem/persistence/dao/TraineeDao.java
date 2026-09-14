package org.vttish.gymcrmsystem.persistence.dao;

import org.vttish.gymcrmsystem.entity.Trainee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Optional<Trainee> selectById(UUID id);
    List<Trainee> selectAll();
    Trainee update(Trainee trainee);
    void delete(UUID id);

    boolean existsByUsername(String username);
}
