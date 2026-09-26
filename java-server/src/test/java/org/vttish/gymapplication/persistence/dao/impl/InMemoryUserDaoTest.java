package org.vttish.gymapplication.persistence.dao.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.entity.Trainee;

class InMemoryUserDaoTest {

  private static class TestUserDao extends InMemoryUserDao<Trainee> {
    @Override
    public UUID getEntityId(Trainee entity) {
      return entity.getUserId();
    }
  }

  private TestUserDao dao;

  @BeforeEach
  void setUp() {
    dao = new TestUserDao();
    Map<UUID, Trainee> storage = new HashMap<>();
    dao.setStorage(storage);
    Trainee entity = new Trainee();
    entity.setUsername("firstName.lastName");
    storage.put(entity.getUserId(), entity);
  }

  @Test
  void existsByUsername_shouldReturnTrue_whenUsernameMatches() {
    assertTrue(dao.existsByUsername("firstName.lastName"));
  }

  @Test
  void existsByUsername_shouldReturnFalse_whenUsernameDoesNotMatch() {
    assertFalse(dao.existsByUsername("nonexistent.username"));
  }
}
