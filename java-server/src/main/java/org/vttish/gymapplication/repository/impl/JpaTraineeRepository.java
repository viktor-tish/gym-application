package org.vttish.gymapplication.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.repository.TraineeRepository;

@Repository
public class JpaTraineeRepository extends JpaBaseRepository<Trainee>
    implements TraineeRepository {

  public JpaTraineeRepository() {
    super(Trainee.class);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public boolean existsByUsername(String username) {
    if (username == null) {
      return false;
    }

    String query = String.format(
        "SELECT COUNT(t) FROM %s t JOIN t.user u WHERE u.username = :username",
        entityClass.getSimpleName()
    );

    Long count = entityManager.createQuery(query, Long.class)
        .setParameter("username", username)
        .getSingleResult();

    return count > 0;
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<Trainee> findAll() {
    String query = String.format(
        "SELECT t FROM %s t JOIN FETCH t.user u",
        entityClass.getSimpleName()
    );

    return entityManager.createQuery(query, entityClass).getResultList();
  }

  @SuppressWarnings("java:S2077")
  @Override
  public Optional<Trainee> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }

    String query = String.format(
        "SELECT t FROM %s t JOIN FETCH t.user u WHERE u.username = :username",
        entityClass.getSimpleName()
    );

    List<Trainee> results = entityManager.createQuery(query, entityClass)
        .setParameter("username", username)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public void deleteByUsername(String username) {
    if (username == null) {
      return;
    }
    findByUsername(username).ifPresent(entity -> entityManager.remove(entity));
  }
}
