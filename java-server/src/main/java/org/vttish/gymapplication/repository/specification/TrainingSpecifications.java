package org.vttish.gymapplication.repository.specification;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

public class TrainingSpecifications {

  private TrainingSpecifications() {
    throw new UnsupportedOperationException();
  }

  public static Specification<Training> forTrainee(
      String traineeUsername, TraineeTrainingsFilter filter
  ) {
    Specification<Training> spec = withFetches().and(traineeUsernameEquals(traineeUsername));

    if (filter == null) {
      return spec;
    }

    if (filter.fromDate() != null) {
      spec = spec.and(dateGreaterThanOrEqual(filter.fromDate()));
    }

    if (filter.toDate() != null) {
      spec = spec.and(dateLessThanOrEqual(filter.toDate()));
    }

    if (filter.trainerName() != null && !filter.trainerName().isBlank()) {
      spec = spec.and(trainerNameMatches(filter.trainerName().trim()));
    }

    if (filter.trainingTypeId() != null) {
      spec = spec.and(trainingTypeIdEquals(filter.trainingTypeId()));
    }

    return spec;
  }

  public static Specification<Training> forTrainer(
      String trainerUsername, TrainerTrainingsFilter filter
  ) {
    Specification<Training> spec = withFetches().and(trainerUsernameEquals(trainerUsername));

    if (filter == null) {
      return spec;
    }

    if (filter.fromDate() != null) {
      spec = spec.and(dateGreaterThanOrEqual(filter.fromDate()));
    }

    if (filter.toDate() != null) {
      spec = spec.and(dateLessThanOrEqual(filter.toDate()));
    }

    if (filter.traineeName() != null && !filter.traineeName().isBlank()) {
      spec = spec.and(traineeNameMatches(filter.traineeName().trim()));
    }

    return spec;
  }

  private static Specification<Training> withFetches() {
    return (root, query, cb) -> {
      if (query != null && query.getResultType() != Long.class) {
        Fetch<Training, Trainee> traineeFetch = root.fetch("trainee", JoinType.INNER);
        traineeFetch.fetch("user", JoinType.INNER);

        Fetch<Training, Trainer> trainerFetch = root.fetch("trainer", JoinType.INNER);
        trainerFetch.fetch("user", JoinType.INNER);
        root.fetch("type", JoinType.INNER);
      }

      return null;
    };
  }

  private static Specification<Training> traineeUsernameEquals(String username) {
    return (root, query, cb) ->
        cb.equal(root.get("trainee").get("user").get("username"), username);
  }

  private static Specification<Training> trainerUsernameEquals(String username) {
    return (root, query, cb) ->
        cb.equal(root.get("trainer").get("user").get("username"), username);
  }

  private static Specification<Training> dateGreaterThanOrEqual(LocalDate fromDate) {
    return (root, query, cb) ->
        cb.greaterThanOrEqualTo(root.get("date"), fromDate);
  }

  private static Specification<Training> dateLessThanOrEqual(LocalDate toDate) {
    return (root, query, cb) ->
        cb.lessThanOrEqualTo(root.get("date"), toDate);
  }

  private static Specification<Training> trainingTypeIdEquals(UUID typeId) {
    return (root, query, cb) ->
        cb.equal(root.get("type").get("id"), typeId);
  }

  private static Specification<Training> trainerNameMatches(String name) {
    return (root, query, cb) -> {
      Path<User> user = root.get("trainer").get("user");

      return cb.or(
          cb.equal(user.get("firstName"), name),
          cb.equal(user.get("lastName"), name),
          cb.equal(user.get("username"), name)
      );
    };
  }

  private static Specification<Training> traineeNameMatches(String name) {
    return (root, query, cb) -> {
      Path<User> user = root.get("trainee").get("user");

      return cb.or(
          cb.equal(user.get("firstName"), name),
          cb.equal(user.get("lastName"), name),
          cb.equal(user.get("username"), name)
      );
    };
  }
}
