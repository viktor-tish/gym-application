package org.vttish.gymapplication.persistence.dao.impl;

import java.util.Map;
import java.util.UUID;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.persistence.dao.UserDao;

public abstract class InMemoryUserDao<T extends User> extends InMemoryBaseDao<T, UUID>
    implements UserDao<T> {
  private Map<UUID, T> storage;

  public void setStorage(Map<UUID, T> storage) {
    this.storage = storage;
    super.setStorage(storage);
  }

  @Override
  public boolean existsByUsername(String username) {
    return storage.values().stream()
        .anyMatch(user -> user.getUsername().equals(username));
  }
}
