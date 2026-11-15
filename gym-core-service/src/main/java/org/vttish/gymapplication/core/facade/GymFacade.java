package org.vttish.gymapplication.core.facade;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.core.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.core.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.core.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.core.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.core.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.core.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.core.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.core.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.core.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;

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
