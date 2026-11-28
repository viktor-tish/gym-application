package org.vttish.gymapplication.core.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.core.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingDetailsResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;

public interface TrainingService {

  TrainingDetailsResponseDto create(CreateTrainingRequestDto request);

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
