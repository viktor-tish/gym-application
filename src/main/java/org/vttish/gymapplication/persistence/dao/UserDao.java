package org.vttish.gymapplication.persistence.dao;

import java.util.UUID;
import org.vttish.gymapplication.entity.User;

public interface UserDao<T extends User> extends BaseDao<T, UUID> {
  boolean existsByUsername(String username);
}
