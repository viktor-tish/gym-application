package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;
import org.vttish.gymapplication.repository.specification.TrainingSpecifications;

public interface TrainingRepository
    extends JpaRepository<Training, UUID>, JpaSpecificationExecutor<Training> {

  default List<Training> findTraineeTrainingsByFilter(
      String traineeUsername, TraineeTrainingsFilter filter
  ) {
    return findAll(TrainingSpecifications.forTrainee(traineeUsername, filter));
  }

  default List<Training> findTrainerTrainingsByFilter(
      String trainerUsername, TrainerTrainingsFilter filter
  ) {
    return findAll(TrainingSpecifications.forTrainer(trainerUsername, filter));
  }
}
