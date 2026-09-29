package org.vttish.gymapplication.facade;

import java.util.List;
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
