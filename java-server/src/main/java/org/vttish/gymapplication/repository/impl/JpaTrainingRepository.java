package org.vttish.gymapplication.repository.impl;

import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.TrainingRepository;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

@Repository
public class JpaTrainingRepository extends JpaBaseRepository<Training>
    implements TrainingRepository {

  public JpaTrainingRepository() {
    super(Training.class);
  }

  @Override
  public List<Training> findTraineeTrainingsByFilter(
      String traineeUsername, TraineeTrainingsFilter filter
  ) {

    StringBuilder queryBuilder = new StringBuilder("SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE tru.username = :traineeUsername");

    appendTraineeConditions(queryBuilder, filter);

    TypedQuery<Training> query = entityManager.createQuery(queryBuilder.toString(), Training.class);
    query.setParameter("traineeUsername", traineeUsername);

    setTraineeParameters(query, filter);

    return query.getResultList();
  }

  @Override
  public List<Training> findTrainerTrainingsByFilter(
      String trainerUsername, TrainerTrainingsFilter filter
  ) {

    StringBuilder queryBuilder = new StringBuilder("SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE trnu.username = :trainerUsername");

    appendTrainerConditions(queryBuilder, filter);

    TypedQuery<Training> query = entityManager.createQuery(queryBuilder.toString(), Training.class);
    query.setParameter("trainerUsername", trainerUsername);

    setTrainerParameters(query, filter);

    return query.getResultList();
  }

  private void appendTraineeConditions(StringBuilder jpql, TraineeTrainingsFilter filter) {
    if (filter == null) {
      return;
    }

    appendDateConditions(jpql, filter.fromDate(), filter.toDate());

    if (filter.trainerName() != null && !filter.trainerName().isBlank()) {
      jpql.append(" AND (trnu.firstName = :trainerName OR trnu.lastName = :trainerName "
          + "OR trnu.username = :trainerName)");
    }

    if (filter.trainingTypeId() != null) {
      jpql.append(" AND ty.id = :trainingTypeId");
    }
  }

  private void setTraineeParameters(TypedQuery<?> query, TraineeTrainingsFilter filter) {
    if (filter == null) {
      return;
    }

    setDateParameters(query, filter.fromDate(), filter.toDate());

    if (filter.trainerName() != null && !filter.trainerName().isBlank()) {
      query.setParameter("trainerName", filter.trainerName());
    }

    if (filter.trainingTypeId() != null) {
      query.setParameter("trainingTypeId", filter.trainingTypeId());
    }
  }

  private void appendTrainerConditions(StringBuilder jpql, TrainerTrainingsFilter filter) {
    if (filter == null) {
      return;
    }

    appendDateConditions(jpql, filter.fromDate(), filter.toDate());

    if (filter.traineeName() != null && !filter.traineeName().isBlank()) {
      jpql.append(" AND (tru.firstName = :traineeName OR tru.lastName = :traineeName "
          + "OR tru.username = :traineeName)");
    }
  }

  private void setTrainerParameters(TypedQuery<?> query, TrainerTrainingsFilter filter) {
    if (filter == null) {
      return;
    }

    setDateParameters(query, filter.fromDate(), filter.toDate());

    if (filter.traineeName() != null && !filter.traineeName().isBlank()) {
      query.setParameter("traineeName", filter.traineeName());
    }
  }

  private void appendDateConditions(StringBuilder jpql, LocalDate fromDate, LocalDate toDate) {
    if (fromDate != null) {
      jpql.append(" AND t.date >= :fromDate");
    }

    if (toDate != null) {
      jpql.append(" AND t.date <= :toDate");
    }
  }

  private void setDateParameters(TypedQuery<?> query, LocalDate fromDate, LocalDate toDate) {
    if (fromDate != null) {
      query.setParameter("fromDate", fromDate);
    }

    if (toDate != null) {
      query.setParameter("toDate", toDate);
    }
  }
}
