package org.vttish.gymapplication.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;

public interface TraineeService {

  CredentialsDto create(@Valid CreateTraineeDto createTraineeDto);

  TraineeDetailsDto getByUsername(@NotBlank(message = "Username is mandatory") String username);

  TraineeDetailsDto update(
      @NotBlank(message = "Username is mandatory") String username,
      @Valid UpdateTraineeDto updateTraineeDto
  );

  void deleteByUsername(@NotBlank(message = "Username is mandatory") String username);

  List<TrainerCardDto> updateTrainersList(
      @NotBlank(message = "Trainee username is mandatory") String traineeUsername,
      List<String> trainerUsernames
  );
}

