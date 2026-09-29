package org.vttish.gymapplication.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.dto.ChangePasswordDto;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;
import org.vttish.gymapplication.facade.GymFacade;
import org.vttish.gymapplication.service.AuthenticationService;
import org.vttish.gymapplication.service.TraineeService;
import org.vttish.gymapplication.service.TrainerService;
import org.vttish.gymapplication.service.TrainingService;

@Component
@RequiredArgsConstructor
public class DefaultGymFacade implements GymFacade {
  private final TraineeService traineeService;
  private final TrainerService trainerService;
  private final TrainingService trainingService;
  private final AuthenticationService authenticationService;

  @Override
  public boolean authenticate(CredentialsDto credentialsDto) {
    return authenticationService.authenticate(credentialsDto);
  }

  @Override
  public void changePassword(ChangePasswordDto changePasswordDto) {
    authenticationService.changePassword(changePasswordDto);
  }

  @Override
  public void toggleActivation(String username) {
    authenticationService.toggleActivation(username);
  }

  @Override
  public CredentialsDto createTrainee(CreateTraineeDto createTraineeDto) {
    return traineeService.create(createTraineeDto);
  }

  @Override
  public TraineeDetailsDto getTraineeByUsername(String username) {
    return traineeService.getByUsername(username);
  }

  @Override
  public TraineeDetailsDto updateTrainee(String username, UpdateTraineeDto updateTraineeDto) {
    return traineeService.update(username, updateTraineeDto);
  }

  @Override
  public void deleteTraineeByUsername(String username) {
    traineeService.deleteByUsername(username);
  }

  @Override
  public List<TrainerCardDto> addTrainerToTrainee(String traineeUsername, String trainerUsername) {
    return traineeService.addTrainer(traineeUsername, trainerUsername);
  }

  @Override
  public List<TrainerCardDto> removeTrainerFromTrainee(
      String traineeUsername, String trainerUsername) {
    return traineeService.removeTrainer(traineeUsername, trainerUsername);
  }

  @Override
  public CredentialsDto createTrainer(CreateTrainerDto createTrainerDto) {
    return trainerService.create(createTrainerDto);
  }

  @Override
  public TrainerDetailsDto getTrainerByUsername(String username) {
    return trainerService.getByUsername(username);
  }

  @Override
  public TrainerDetailsDto updateTrainer(String username, UpdateTrainerDto updateTrainerDto) {
    return trainerService.update(username, updateTrainerDto);
  }

  @Override
  public List<TrainerCardDto> getUnassignedTrainers(String traineeUsername) {
    return trainerService.getUnassigned(traineeUsername);
  }

  @Override
  public void createTraining(CreateTrainingDto createTrainingDto) {
    trainingService.create(createTrainingDto);
  }

  @Override
  public List<TrainingDetailsDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingFilterDto filter
  ) {
    return trainingService.getTraineeTrainings(traineeUsername, filter);
  }

  @Override
  public List<TrainingDetailsDto> getTrainerTrainings(
      String trainerUsername, TrainerTrainingFilterDto filter
  ) {
    return trainingService.getTrainerTrainings(trainerUsername, filter);
  }
}
