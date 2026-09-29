package org.vttish.gymapplication.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;

public interface TrainingService {

  void create(@Valid CreateTrainingDto createTrainingDto);

  List<TrainingDetailsDto> getTraineeTrainings(
      @NotBlank(message = "Trainee username is mandatory") String traineeUsername,
      @Valid TraineeTrainingFilterDto filter
  );

  List<TrainingDetailsDto> getTrainerTrainings(
      @NotBlank(message = "Trainer username is mandatory") String trainerUsername,
      @Valid TrainerTrainingFilterDto filter
  );
}
