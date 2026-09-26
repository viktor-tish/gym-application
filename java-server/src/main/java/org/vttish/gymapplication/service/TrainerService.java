package org.vttish.gymapplication.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;

public interface TrainerService {
  TrainerDetailsDto create(CreateTrainerDto createTrainerDto);

  TrainerDetailsDto getById(UUID id);

  List<TrainerCardDto> getAll();

  TrainerDetailsDto update(UpdateTrainerDto updateTrainerDto);
}
