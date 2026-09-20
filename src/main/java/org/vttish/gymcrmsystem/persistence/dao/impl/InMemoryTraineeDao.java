package org.vttish.gymcrmsystem.persistence.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;

@Component
public class InMemoryTraineeDao implements TraineeDao {
  private Map<UUID, Trainee> traineeStorage;

  @Autowired
  public void setTraineeStorage(Map<UUID, Trainee> traineeStorage) {
    this.traineeStorage = traineeStorage;
  }

  @Override
  public Trainee create(Trainee trainee) {
    Assert.notNull(trainee, "Trainee must not be null");
    Assert.notNull(trainee.getUserId(), "Trainee ID must not be null");
    Assert.isTrue(!traineeStorage.containsKey(trainee.getUserId()),
        "Trainee already exists with such ID");

    traineeStorage.put(trainee.getUserId(), trainee);
    return trainee;
  }

  @Override
  public Optional<Trainee> selectById(UUID id) {
    Assert.notNull(id, "Trainee ID must not be null");

    return Optional.ofNullable(traineeStorage.get(id));
  }

  @Override
  public List<Trainee> selectAll() {
    return new ArrayList<>(traineeStorage.values());
  }

  @Override
  public void delete(UUID id) {
    Assert.notNull(id, "Trainee ID must not be null");

    traineeStorage.remove(id);
  }

  @Override
  public boolean existsById(UUID id) {
    Assert.notNull(id, "Trainee ID must not be null");

    return traineeStorage.containsKey(id);
  }

  @Override
  public boolean existsByUsername(String username) {
    return traineeStorage.values().stream()
        .anyMatch(trainee -> trainee.getUsername().equals(username));
  }
}
