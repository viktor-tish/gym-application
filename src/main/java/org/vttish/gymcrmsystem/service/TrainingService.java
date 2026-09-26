package org.vttish.gymcrmsystem.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymcrmsystem.dto.CreateTrainingDto;
import org.vttish.gymcrmsystem.dto.TrainingCardDto;
import org.vttish.gymcrmsystem.dto.TrainingDetailsDto;

public interface TrainingService {
  TrainingDetailsDto create(CreateTrainingDto createTrainingDto);

  TrainingDetailsDto getById(UUID id);

  List<TrainingCardDto> getAll();
}
