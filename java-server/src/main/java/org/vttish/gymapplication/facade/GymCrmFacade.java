package org.vttish.gymapplication.facade;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.TraineeCardDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.TrainingCardDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;
import org.vttish.gymapplication.service.TraineeService;
import org.vttish.gymapplication.service.TrainerService;
import org.vttish.gymapplication.service.TrainingService;

@RequiredArgsConstructor
@Component
public class GymCrmFacade {
  private final TraineeService traineeService;
  private final TrainerService trainerService;
  private final TrainingService trainingService;

  public TraineeDetailsDto createTrainee(CreateTraineeDto createTraineeDto) {
    return traineeService.create(createTraineeDto);
  }

  public TraineeDetailsDto updateTrainee(UpdateTraineeDto updateTraineeDto) {
    return traineeService.update(updateTraineeDto);
  }

  public void deleteTrainee(UUID id) {
    traineeService.delete(id);
  }

  public TraineeDetailsDto getTraineeById(UUID id) {
    return traineeService.getById(id);
  }

  public List<TraineeCardDto> getAllTrainees() {
    return traineeService.getAll();
  }

  public TrainerDetailsDto createTrainer(CreateTrainerDto createTrainerDto) {
    return trainerService.create(createTrainerDto);
  }

  public TrainerDetailsDto updateTrainer(UpdateTrainerDto updateTrainerDto) {
    return trainerService.update(updateTrainerDto);
  }

  public TrainerDetailsDto getTrainerById(UUID id) {
    return trainerService.getById(id);
  }

  public List<TrainerCardDto> getAllTrainers() {
    return trainerService.getAll();
  }

  public TrainingDetailsDto createTraining(CreateTrainingDto createTrainingDto) {
    return trainingService.create(createTrainingDto);
  }

  public TrainingDetailsDto getTrainingById(UUID id) {
    return trainingService.getById(id);
  }

  public List<TrainingCardDto> getAllTrainings() {
    return trainingService.getAll();
  }
}
