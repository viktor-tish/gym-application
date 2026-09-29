package org.vttish.gymapplication.repository.impl;

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.dto.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.TrainerTrainingFilterDto;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.TrainingRepository;
import org.vttish.gymapplication.validation.DateRange;

@Repository
public class JpaTrainingRepository extends JpaBaseRepository<Training>
    implements TrainingRepository {

  public JpaTrainingRepository() {
    super(Training.class);
  }

  @Override
  public List<Training> findTraineeTrainingsByFilter(
      String traineeUsername, TraineeTrainingFilterDto filter
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
      String trainerUsername, TrainerTrainingFilterDto filter
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

  private void appendTraineeConditions(StringBuilder jpql, TraineeTrainingFilterDto filter) {
    if (filter == null) {
      return;
    }

    appendDateConditions(jpql, filter);

    if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
      jpql.append(" AND (trnu.firstName = :trainerName OR trnu.lastName = :trainerName "
          + "OR trnu.username = :trainerName)");
    }

    if (filter.getTrainingTypeId() != null) {
      jpql.append(" AND ty.id = :trainingTypeId");
    }
  }

  private void setTraineeParameters(TypedQuery<?> query, TraineeTrainingFilterDto filter) {
    if (filter == null) {
      return;
    }

    setDateParameters(query, filter);

    if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
      query.setParameter("trainerName", filter.getTrainerName());
    }

    if (filter.getTrainingTypeId() != null) {
      query.setParameter("trainingTypeId", filter.getTrainingTypeId());
    }
  }

  private void appendTrainerConditions(StringBuilder jpql, TrainerTrainingFilterDto filter) {
    if (filter == null) {
      return;
    }

    appendDateConditions(jpql, filter);

    if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
      jpql.append(" AND (tru.firstName = :traineeName OR tru.lastName = :traineeName "
          + "OR tru.username = :traineeName)");
    }
  }

  private void setTrainerParameters(TypedQuery<?> query, TrainerTrainingFilterDto filter) {
    if (filter == null) {
      return;
    }

    setDateParameters(query, filter);

    if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
      query.setParameter("traineeName", filter.getTraineeName());
    }
  }

  private void appendDateConditions(StringBuilder jpql, DateRange filter) {
    if (filter.getFromDate() != null) {
      jpql.append(" AND t.date >= :fromDate");
    }

    if (filter.getToDate() != null) {
      jpql.append(" AND t.date <= :toDate");
    }
  }

  private void setDateParameters(TypedQuery<?> query, DateRange filter) {
    if (filter.getFromDate() != null) {
      query.setParameter("fromDate", filter.getFromDate());
    }

    if (filter.getToDate() != null) {
      query.setParameter("toDate", filter.getToDate());
    }
  }
}
