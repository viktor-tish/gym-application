package org.vttish.gymapplication.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.CredentialsDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainee.CreateTraineeDto;
import org.vttish.gymapplication.dto.trainee.TraineeDetailsDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeDto;
import org.vttish.gymapplication.dto.trainer.CreateTrainerDto;
import org.vttish.gymapplication.dto.trainer.TrainerCardDto;
import org.vttish.gymapplication.dto.trainer.TrainerDetailsDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerDto;
import org.vttish.gymapplication.dto.training.CreateTrainingDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainingDetailsDto;
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
  public void login(LoginRequestDto request) {
    authenticationService.login(request);
  }

  @Override
  public void changePassword(ChangePasswordRequestDto request) {
    authenticationService.changePassword(request);
  }

  @Override
  public void updateStatus(String username, UpdateUserStatusRequestDto request) {
    authenticationService.updateStatus(username, request);
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
