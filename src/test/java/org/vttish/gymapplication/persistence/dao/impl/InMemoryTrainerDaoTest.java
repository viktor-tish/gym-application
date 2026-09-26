package org.vttish.gymapplication.persistence.dao.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.entity.Trainer;

class InMemoryTrainerDaoTest {

  private InMemoryTrainerDao trainerDao;
  private Map<UUID, Trainer> storage;
  private Trainer trainer;

  @BeforeEach
  void setUp() {
    trainerDao = new InMemoryTrainerDao();
    storage = new HashMap<>();
    trainerDao.setTrainerStorage(storage);
    trainer = new Trainer();
  }

  @Test
  void getEntityId_shouldReturnUserId() {
    assertSame(trainer.getUserId(), trainerDao.getEntityId(trainer));
  }

  @Test
  void setTrainerStorage_shouldWireStorageCorrectly() {
    trainerDao.create(trainer);

    assertTrue(storage.containsKey(trainer.getUserId()));
  }
}
