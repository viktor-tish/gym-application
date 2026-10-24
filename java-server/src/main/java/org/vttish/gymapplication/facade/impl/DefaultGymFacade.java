package org.vttish.gymapplication.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
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
  public LoginResponseDto login(LoginRequestDto request) {
    return authenticationService.login(request);
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
  public CredentialsResponseDto registerTrainee(RegisterTraineeRequestDto request) {
    return traineeService.register(request);
  }

  @Override
  public TraineeProfileResponseDto getTraineeProfile(String username) {
    return traineeService.getProfile(username);
  }

  @Override
  public UpdateTraineeProfileResponseDto updateTraineeProfile(
      String username,
      UpdateTraineeProfileRequestDto request
  ) {
    return traineeService.updateProfile(username, request);
  }

  @Override
  public void deleteTrainee(String username) {
    traineeService.delete(username);
  }

  @Override
  public List<TrainerItemResponseDto> updateTraineeTrainers(
      String username,
      UpdateTraineeTrainersRequestDto request
  ) {
    return traineeService.updateTrainers(username, request);
  }

  @Override
  public CredentialsResponseDto registerTrainer(RegisterTrainerRequestDto request) {
    return trainerService.register(request);
  }

  @Override
  public TrainerProfileResponseDto getTrainerProfile(String username) {
    return trainerService.getProfile(username);
  }

  @Override
  public UpdateTrainerProfileResponseDto updateTrainerProfile(
      String username,
      UpdateTrainerProfileRequestDto request
  ) {
    return trainerService.updateProfile(username, request);
  }

  @Override
  public List<TrainerItemResponseDto> getUnassignedActiveTrainers(String traineeUsername) {
    return trainerService.getUnassignedActiveTrainers(traineeUsername);
  }

  @Override
  public void createTraining(CreateTrainingRequestDto request) {
    trainingService.create(request);
  }

  @Override
  public List<TraineeTrainingItemResponseDto> getTraineeTrainings(
      String traineeUsername,
      GetTraineeTrainingsRequestDto request
  ) {
    return trainingService.getTraineeTrainings(traineeUsername, request);
  }

  @Override
  public List<TrainerTrainingItemResponseDto> getTrainerTrainings(
      String trainerUsername,
      GetTrainerTrainingsRequestDto request
  ) {
    return trainingService.getTrainerTrainings(trainerUsername, request);
  }

  @Override
  public List<TrainingTypeResponseDto> getTrainingTypes() {
    return trainingService.getTypes();
  }
}
