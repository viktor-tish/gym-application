package org.vttish.gymcrmsystem.persistence.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.vttish.gymcrmsystem.entity.Training;
import org.vttish.gymcrmsystem.persistence.dao.TrainingDao;

@Component
public class InMemoryTrainingDao implements TrainingDao {
  private Map<UUID, Training> trainingStorage;

  @Autowired
  public void setTrainingStorage(Map<UUID, Training> trainingStorage) {
    this.trainingStorage = trainingStorage;
  }

  @Override
  public Training create(Training training) {
    Assert.notNull(training, "Training must not be null");
    Assert.notNull(training.getId(), "Training ID must not be null");
    Assert.isTrue(!trainingStorage.containsKey(training.getId()),
        "Training already exists with such ID");

    trainingStorage.put(training.getId(), training);
    return training;
  }

  @Override
  public Optional<Training> selectById(UUID id) {
    Assert.notNull(id, "Training ID must not be null");

    return Optional.ofNullable(trainingStorage.get(id));
  }

  @Override
  public List<Training> selectAll() {
    return new ArrayList<>(trainingStorage.values());
  }
}
