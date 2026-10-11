package org.vttish.gymapplication.facade;

import java.util.List;
import org.vttish.gymapplication.dto.auth.ChangePasswordDto;
import org.vttish.gymapplication.dto.auth.CredentialsDto;
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

public interface GymFacade {

  boolean authenticate(CredentialsDto credentialsDto);

  void changePassword(ChangePasswordDto changePasswordDto);

  void toggleActivation(String username);


  CredentialsDto createTrainee(CreateTraineeDto createTraineeDto);

  TraineeDetailsDto getTraineeByUsername(String username);

  TraineeDetailsDto updateTrainee(String username, UpdateTraineeDto updateTraineeDto);

  void deleteTraineeByUsername(String username);

  List<TrainerCardDto> addTrainerToTrainee(String traineeUsername, String trainerUsername);

  List<TrainerCardDto> removeTrainerFromTrainee(String traineeUsername, String trainerUsername);


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
