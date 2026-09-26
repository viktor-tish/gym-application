package org.vttish.gymapplication.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.TrainingCardDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;

public interface TrainingService {
  TrainingDetailsDto create(CreateTrainingDto createTrainingDto);

  TrainingDetailsDto getById(UUID id);

  List<TrainingCardDto> getAll();
}
