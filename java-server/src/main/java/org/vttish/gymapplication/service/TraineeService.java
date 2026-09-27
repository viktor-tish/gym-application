package org.vttish.gymapplication.service;

import java.util.List;
import java.util.UUID;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.TraineeCardDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;

public interface TraineeService {
  TraineeDetailsDto create(CreateTraineeDto createTraineeDto);

  TraineeDetailsDto getById(UUID id);

  List<TraineeCardDto> getAll();

  TraineeDetailsDto update(UpdateTraineeDto updateTraineeDto);

  void delete(UUID id);
}
