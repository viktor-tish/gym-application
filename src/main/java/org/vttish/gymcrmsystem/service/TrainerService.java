package org.vttish.gymcrmsystem.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymcrmsystem.dto.CreateTrainerDto;
import org.vttish.gymcrmsystem.dto.TrainerCardDto;
import org.vttish.gymcrmsystem.dto.TrainerDetailsDto;
import org.vttish.gymcrmsystem.dto.UpdateTrainerDto;

public interface TrainerService {
  TrainerDetailsDto create(CreateTrainerDto createTrainerDto);

  TrainerDetailsDto getById(UUID id);

  List<TrainerCardDto> getAll();

  TrainerDetailsDto update(UpdateTrainerDto updateTrainerDto);
}
