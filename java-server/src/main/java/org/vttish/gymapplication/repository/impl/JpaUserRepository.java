package org.vttish.gymapplication.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.UserJpaEntity;
import org.vttish.gymapplication.repository.UserRepository;

@Repository
public class JpaUserRepository extends JpaBaseRepository<UserJpaEntity>
    implements UserRepository {

  public JpaUserRepository() {
    super(UserJpaEntity.class);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public boolean existsByUsername(String username) {
    if (username == null) {
      return false;
    }

    String query = String.format(
        "SELECT COUNT(u) FROM %s u WHERE u.username = :username",
        entityClass.getSimpleName()
    );

    Long count = entityManager.createQuery(query, Long.class)
        .setParameter("username", username)
        .getSingleResult();

    return count > 0;
  }

  @SuppressWarnings("java:S2077")
  @Override
  public Optional<UserJpaEntity> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }

    String query = String.format(
        "SELECT u FROM %s u WHERE u.username = :username",
        entityClass.getSimpleName()
    );

    List<UserJpaEntity> results = entityManager.createQuery(query, entityClass)
        .setParameter("username", username)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<String> findUsernamesByPrefix(String prefix) {
    if (prefix == null) {
      return List.of();
    }

    String query = String.format(
        "SELECT u.username FROM %s u WHERE u.username LIKE :prefix",
        entityClass.getSimpleName()
    );

    return entityManager.createQuery(query, String.class)
        .setParameter("prefix", prefix + "%")
        .getResultList();
  }
}
