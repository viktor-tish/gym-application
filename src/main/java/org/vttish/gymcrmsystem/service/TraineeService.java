package org.vttish.gymcrmsystem.service;

import org.vttish.gymcrmsystem.dto.CreateTraineeDto;
import org.vttish.gymcrmsystem.dto.TraineeCardDto;
import org.vttish.gymcrmsystem.dto.TraineeDetailsDto;
import org.vttish.gymcrmsystem.dto.UpdateTraineeDto;

import java.util.List;
import java.util.UUID;

public interface TraineeService {
    TraineeDetailsDto create(CreateTraineeDto createTraineeDto);
    TraineeDetailsDto getById(UUID id);
    List<TraineeCardDto> getAll();
    TraineeDetailsDto update(UpdateTraineeDto updateTraineeDto);
    void delete(UUID id);
}
