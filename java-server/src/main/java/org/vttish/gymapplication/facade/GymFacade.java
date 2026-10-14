package org.vttish.gymapplication.facade;

import java.util.List;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.CredentialsDto;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.CreateTrainerDto;
import org.vttish.gymapplication.dto.trainer.TrainerCardDto;
import org.vttish.gymapplication.dto.trainer.TrainerDetailsDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerDto;
import org.vttish.gymapplication.dto.training.CreateTrainingDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainingDetailsDto;

public interface GymFacade {

  void login(LoginRequestDto request);

  void changePassword(ChangePasswordRequestDto request);

  void updateStatus(String username, UpdateUserStatusRequestDto request);

  CredentialsResponseDto registerTrainee(RegisterTraineeRequestDto request);

  TraineeProfileResponseDto getTraineeProfile(String username);

  UpdateTraineeProfileResponseDto updateTraineeProfile(
      String username,
      UpdateTraineeProfileRequestDto request
  );

  void deleteTrainee(String username);

  List<TrainerItemResponseDto> updateTraineeTrainers(
      String username,
      UpdateTraineeTrainersRequestDto request
  );

  CredentialsDto createTrainer(CreateTrainerDto createTrainerDto);

  TrainerDetailsDto getTrainerByUsername(String username);

  TrainerDetailsDto updateTrainer(String username, UpdateTrainerDto updateTrainerDto);

  List<TrainerCardDto> getUnassignedTrainers(String traineeUsername);

  void createTraining(CreateTrainingDto createTrainingDto);

  List<TrainingDetailsDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingFilterDto filter);

  List<TrainingDetailsDto> getTrainerTrainings(
      String trainerUsername, TrainerTrainingFilterDto filter);
}
