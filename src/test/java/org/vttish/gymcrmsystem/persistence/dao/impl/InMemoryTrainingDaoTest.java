package org.vttish.gymcrmsystem.persistence.dao.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymcrmsystem.entity.Training;

class InMemoryTrainingDaoTest {

  private InMemoryTrainingDao trainingDao;
  private Map<UUID, Training> storage;
  private Training training;

  @BeforeEach
  void setUp() {
    trainingDao = new InMemoryTrainingDao();
    storage = new HashMap<>();
    trainingDao.setTrainingStorage(storage);
    training = new Training();
  }

  @Test
  void getEntityId_shouldReturnId() {
    assertSame(training.getId(), trainingDao.getEntityId(training));
  }

  @Test
  void setTrainingStorage_shouldWireStorageCorrectly() {
    trainingDao.create(training);

    assertTrue(storage.containsKey(training.getId()));
  }
}
