package org.vttish.gymapplication.persistence.dao.impl;

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
import org.vttish.gymapplication.entity.Training;

class InMemoryBaseDaoTest {

  private static class TestBaseDao extends InMemoryBaseDao<Training, UUID> {
    @Override
    public UUID getEntityId(Training entity) {
      return entity.getId();
    }
  }

  private TestBaseDao dao;
  private Map<UUID, Training> storage;
  private Training entity;
  private Training anotherEntity;

  @BeforeEach
  void setUp() {
    dao = new TestBaseDao();
    storage = new HashMap<>();
    dao.setStorage(storage);
    entity = new Training();
    anotherEntity = new Training();
  }

  @Test
  void create_shouldAddEntity_whenValidAndNew() {
    Training result = dao.create(entity);

    assertSame(entity, result);
    assertTrue(storage.containsKey(entity.getId()));
    assertEquals(1, storage.size());
  }

  @Test
  void create_shouldThrowException_whenIdAlreadyExists() {
    storage.put(entity.getId(), entity);

    assertThrows(IllegalArgumentException.class, () -> dao.create(entity));
  }

  @Test
  void create_shouldThrowException_whenEntityIsNull() {
    assertThrows(IllegalArgumentException.class, () -> dao.create(null));
  }

  @Test
  void selectById_shouldReturnEntity_whenExists() {
    storage.put(entity.getId(), entity);

    Optional<Training> result = dao.selectById(entity.getId());

    assertTrue(result.isPresent());
    assertSame(entity, result.get());
  }

  @Test
  void selectById_shouldReturnEmpty_whenDoesNotExist() {
    Optional<Training> result = dao.selectById(UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  void selectById_shouldThrowException_whenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> dao.selectById(null));
  }

  @Test
  void selectAll_shouldReturnAllEntities() {
    storage.put(entity.getId(), entity);
    storage.put(anotherEntity.getId(), anotherEntity);

    List<Training> result = dao.selectAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(entity));
    assertTrue(result.contains(anotherEntity));
  }

  @Test
  void existsById_shouldReturnTrue_whenIdMatches() {
    storage.put(entity.getId(), entity);

    assertTrue(dao.existsById(entity.getId()));
  }

  @Test
  void existsById_shouldReturnFalse_whenIdDoesNotMatch() {
    storage.put(entity.getId(), entity);

    assertFalse(dao.existsById(UUID.randomUUID()));
  }

  @Test
  void existsById_shouldThrowException_whenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> dao.existsById(null));
  }

  @Test
  void delete_shouldRemoveEntity_whenExists() {
    storage.put(entity.getId(), entity);

    dao.delete(entity.getId());

    assertFalse(storage.containsKey(entity.getId()));
    assertEquals(0, storage.size());
  }

  @Test
  void delete_shouldNotFail_whenEntityDoesNotExist() {
    assertDoesNotThrow(() -> dao.delete(UUID.randomUUID()));
  }

  @Test
  void delete_shouldThrowException_whenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> dao.delete(null));
  }
}
