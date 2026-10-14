package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

public interface TrainingRepository extends BaseRepository<Training, UUID> {
  List<Training> findTraineeTrainingsByFilter(
      String traineeUsername, TraineeTrainingsFilter filter
  );

  List<Training> findTrainerTrainingsByFilter(
      String trainerUsername, TrainerTrainingsFilter filter
  );
}
