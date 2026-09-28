package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.vttish.gymapplication.entity.UserJpaEntity;

public interface UserRepository extends BaseRepository<UserJpaEntity, UUID> {
  boolean existsByUsername(String username);

  Optional<UserJpaEntity> findByUsername(String username);

  List<String> findUsernamesByPrefix(String prefix);
}
