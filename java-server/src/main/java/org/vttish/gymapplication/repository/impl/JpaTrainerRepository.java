package org.vttish.gymapplication.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.repository.TrainerRepository;

@Repository
public class JpaTrainerRepository extends JpaBaseRepository<Trainer>
    implements TrainerRepository {

  public JpaTrainerRepository() {
    super(Trainer.class);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<Trainer> findAll() {
    String query = String.format(
        "SELECT tr FROM %s tr JOIN FETCH tr.user u JOIN FETCH tr.specialization s",
        entityClass.getSimpleName()
    );

    return entityManager.createQuery(query, entityClass).getResultList();
  }

  @SuppressWarnings("java:S2077")
  @Override
  public Optional<Trainer> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }

    String query = String.format(
        "SELECT t FROM %s t "
            + "JOIN FETCH t.user u "
            + "JOIN FETCH t.specialization s "
            + "WHERE u.username = :username",
        entityClass.getSimpleName());

    List<Trainer> results = entityManager.createQuery(query, entityClass)
        .setParameter("username", username)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<Trainer> findAllByUsernameIn(List<String> usernames) {
    if (usernames == null || usernames.isEmpty()) {
      return List.of();
    }

    String query = String.format(
        "SELECT tr FROM %s tr "
            + "JOIN FETCH tr.user u "
            + "JOIN FETCH tr.specialization s "
            + "WHERE u.username IN (:usernames)",
        entityClass.getSimpleName()
    );

    return entityManager.createQuery(query, entityClass)
        .setParameter("usernames", usernames)
        .getResultList();
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername) {
    if (traineeUsername == null) {
      return List.of();
    }

    String query = String.format(
        "SELECT tr FROM %s tr "
            + "JOIN FETCH tr.user u "
            + "JOIN FETCH tr.specialization s "
            + "WHERE tr NOT IN ("
            + " SELECT assignedTr FROM %s te "
            + "  JOIN te.trainers assignedTr "
            + "  JOIN te.user tu "
            + "  WHERE tu.username = :traineeUsername"
            + ")",
        entityClass.getSimpleName(),
        Trainee.class.getSimpleName()
    );

    return entityManager.createQuery(query, entityClass)
        .setParameter("traineeUsername", traineeUsername)
        .getResultList();
  }
}
