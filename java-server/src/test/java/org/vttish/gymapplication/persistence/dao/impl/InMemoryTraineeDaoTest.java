package org.vttish.gymapplication.persistence.dao.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.entity.Trainee;

class InMemoryTraineeDaoTest {

  private InMemoryTraineeDao traineeDao;
  private Map<UUID, Trainee> storage;
  private Trainee trainee;

  @BeforeEach
  void setUp() {
    traineeDao = new InMemoryTraineeDao();
    storage = new HashMap<>();
    traineeDao.setTraineeStorage(storage);
    trainee = new Trainee();
  }

  @Test
  void getEntityId_shouldReturnUserId() {
    assertSame(trainee.getUserId(), traineeDao.getEntityId(trainee));
  }

  @Test
  void setTraineeStorage_shouldWireStorageCorrectly() {
    traineeDao.create(trainee);

    assertTrue(storage.containsKey(trainee.getUserId()));
  }
}
