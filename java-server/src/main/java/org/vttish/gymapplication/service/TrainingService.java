package org.vttish.gymapplication.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;

public interface TrainingService {

  void create(CreateTrainingRequestDto request);

  void delete(UUID id, String trainerUsername);

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
