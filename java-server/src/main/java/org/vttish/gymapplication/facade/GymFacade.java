package org.vttish.gymapplication.facade;

import java.util.List;
import java.util.UUID;
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

public interface GymFacade {

  LoginResponseDto login(LoginRequestDto request, String ip);

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

  CredentialsResponseDto registerTrainer(RegisterTrainerRequestDto request);

  TrainerProfileResponseDto getTrainerProfile(String username);

  UpdateTrainerProfileResponseDto updateTrainerProfile(
      String username,
      UpdateTrainerProfileRequestDto request
  );

  List<TrainerItemResponseDto> getUnassignedActiveTrainers(String traineeUsername);

  void createTraining(CreateTrainingRequestDto request);

  void deleteTraining(UUID id, String trainerUsername);

  List<TraineeTrainingItemResponseDto> getTraineeTrainings(
      String traineeUsername,
      GetTraineeTrainingsRequestDto request
  );

  List<TrainerTrainingItemResponseDto> getTrainerTrainings(
      String trainerUsername,
      GetTrainerTrainingsRequestDto request
  );

  List<TrainingTypeResponseDto> getTrainingTypes();
}
