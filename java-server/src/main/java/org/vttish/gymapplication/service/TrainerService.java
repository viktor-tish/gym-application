package org.vttish.gymapplication.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;

public interface TrainerService {
  CredentialsDto create(@Valid CreateTrainerDto createTrainerDto);

  TrainerDetailsDto getByUsername(
      @NotBlank(message = "Username is mandatory") String username
  );

  TrainerDetailsDto update(
      @NotBlank(message = "Username is mandatory") String username,
      @Valid UpdateTrainerDto updateTrainerDto
  );

  List<TrainerCardDto> getUnassigned(
      @NotBlank(message = "Trainee username is mandatory") String traineeUsername
  );
}
