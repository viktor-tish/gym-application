package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.User;

public interface UserRepository extends BaseRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  List<String> findUsernamesByPrefix(String prefix);
}
