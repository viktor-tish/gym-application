package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.dto.training.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingFilterDto;
import org.vttish.gymapplication.entity.Training;

public interface TrainingRepository extends BaseRepository<Training, UUID> {

  List<Training> findTraineeTrainingsByFilter(
      String traineeUsername, TraineeTrainingFilterDto filter
  );

  List<Training> findTrainerTrainingsByFilter(
      String trainerUsername, TrainerTrainingFilterDto filter
  );
}
