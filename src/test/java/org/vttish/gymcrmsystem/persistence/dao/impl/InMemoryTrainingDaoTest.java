package org.vttish.gymcrmsystem.persistence.dao.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymcrmsystem.entity.Training;

class InMemoryTrainingDaoTest {

  private InMemoryTrainingDao trainingDao;
  private Map<UUID, Training> testStorage;
  private Training testTraining;

  @BeforeEach
  public void setUp() {
    trainingDao = new InMemoryTrainingDao();
    testStorage = new HashMap<>();
    trainingDao.setTrainingStorage(testStorage);

    testTraining = new Training();
    testTraining.setTrainingName("trainingName");
  }

  @Test
  public void create_shouldAddTraining_whenValidAndNew() {
    Training result = trainingDao.create(testTraining);

    assertSame(testTraining, result);
    assertTrue(testStorage.containsKey(testTraining.getId()));
    assertEquals(1, testStorage.size());
  }

  @Test
  public void create_shouldThrowException_whenIdAlreadyExists() {
    testStorage.put(testTraining.getId(), testTraining);

    assertThrows(IllegalArgumentException.class, () -> trainingDao.create(testTraining));
  }

  @Test
  public void create_shouldThrowException_whenTrainingIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainingDao.create(null));
  }

  @Test
  public void selectById_shouldReturnTraining_whenExists() {
    testStorage.put(testTraining.getId(), testTraining);

    Optional<Training> result = trainingDao.selectById(testTraining.getId());

    assertTrue(result.isPresent());
    assertSame(testTraining, result.get());
  }

  @Test
  public void selectById_shouldReturnEmpty_whenDoesNotExist() {
    Optional<Training> result = trainingDao.selectById(UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  public void selectById_shouldThrowException_whenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainingDao.selectById(null));
  }

  @Test
  public void selectAll_shouldReturnAllTrainings() {
    Training training2 = new Training();
    testStorage.put(testTraining.getId(), testTraining);
    testStorage.put(training2.getId(), training2);

    List<Training> result = trainingDao.selectAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(testTraining));
    assertTrue(result.contains(training2));
  }

  @Test
  public void delete_shouldRemoveTraining_whenExists() {
    testStorage.put(testTraining.getId(), testTraining);

    trainingDao.delete(testTraining.getId());

    assertFalse(testStorage.containsKey(testTraining.getId()));
    assertEquals(0, testStorage.size());
  }

  @Test
  public void delete_shouldNotFail_whenDoesNotExist() {
    assertDoesNotThrow(() -> trainingDao.delete(UUID.randomUUID()));
  }

  @Test
  public void existsById_shouldReturnTrue_whenIdMatches() {
    testStorage.put(testTraining.getId(), testTraining);

    boolean exists = trainingDao.existsById(testTraining.getId());

    assertTrue(exists);
  }

  @Test
  public void existsById_shouldReturnFalse_whenIdDoesNotMatch() {
    testStorage.put(testTraining.getId(), testTraining);

    boolean exists = trainingDao.existsById(UUID.randomUUID());

    assertFalse(exists);
  }
}
