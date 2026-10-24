package org.vttish.gymapplication.service;

import java.util.List;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;

public interface TrainingService {
  void create(CreateTrainingRequestDto request);

  List<TraineeTrainingItemResponseDto> getTraineeTrainings(
      String traineeUsername,
      GetTraineeTrainingsRequestDto request
  );

  List<TrainerTrainingItemResponseDto> getTrainerTrainings(
      String trainerUsername,
      GetTrainerTrainingsRequestDto request
  );

  List<TrainingTypeResponseDto> getTypes();
}
