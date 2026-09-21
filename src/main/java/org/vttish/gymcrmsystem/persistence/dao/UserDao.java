package org.vttish.gymcrmsystem.persistence.dao;

import java.util.UUID;
import org.vttish.gymcrmsystem.entity.User;

public interface UserDao<T extends User> extends BaseDao<T, UUID> {
  boolean existsByUsername(String username);
}
