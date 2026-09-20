package org.vttish.gymcrmsystem.persistence.dao.impl;

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
import org.vttish.gymcrmsystem.entity.Trainer;

class InMemoryTrainerDaoTest {

  private InMemoryTrainerDao trainerDao;
  private Map<UUID, Trainer> testStorage;
  private Trainer testTrainer;

  @BeforeEach
  public void setUp() {
    trainerDao = new InMemoryTrainerDao();
    testStorage = new HashMap<>();
    trainerDao.setTrainerStorage(testStorage);

    testTrainer = new Trainer();
    testTrainer.setFirstName("firstName");
    testTrainer.setLastName("lastName");
    testTrainer.setUsername("firstName.lastName");
  }

  @Test
  public void create_shouldAddTrainer_whenValidAndNew() {
    Trainer result = trainerDao.create(testTrainer);

    assertSame(testTrainer, result);
    assertTrue(testStorage.containsKey(testTrainer.getUserId()));
    assertEquals(1, testStorage.size());
  }

  @Test
  public void create_shouldThrowException_whenIdAlreadyExists() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    assertThrows(IllegalArgumentException.class, () -> trainerDao.create(testTrainer));
  }

  @Test
  public void create_shouldThrowException_whenTrainerIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainerDao.create(null));
  }

  @Test
  public void selectById_shouldReturnTrainer_whenExists() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    Optional<Trainer> result = trainerDao.selectById(testTrainer.getUserId());

    assertTrue(result.isPresent());
    assertSame(testTrainer, result.get());
  }

  @Test
  public void selectById_shouldReturnEmpty_whenDoesNotExist() {
    Optional<Trainer> result = trainerDao.selectById(UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  public void selectById_shouldThrowException_whenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainerDao.selectById(null));
  }

  @Test
  public void selectAll_shouldReturnAllTrainers() {
    Trainer trainer2 = new Trainer();
    testStorage.put(testTrainer.getUserId(), testTrainer);
    testStorage.put(trainer2.getUserId(), trainer2);

    List<Trainer> result = trainerDao.selectAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(testTrainer));
    assertTrue(result.contains(trainer2));
  }

  @Test
  public void existsById_shouldReturnTrue_whenIdMatches() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    boolean exists = trainerDao.existsById(testTrainer.getUserId());

    assertTrue(exists);
  }

  @Test
  public void existsById_shouldReturnFalse_whenIdDoesNotMatch() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    boolean exists = trainerDao.existsById(UUID.randomUUID());

    assertFalse(exists);
  }

  @Test
  public void existsByUsername_shouldReturnTrue_whenUsernameMatches() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    boolean exists = trainerDao.existsByUsername("firstName.lastName");

    assertTrue(exists);
  }

  @Test
  public void existsByUsername_shouldReturnFalse_whenUsernameDoesNotMatch() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    boolean exists = trainerDao.existsByUsername("newFirstName.lastName");

    assertFalse(exists);
  }
}
