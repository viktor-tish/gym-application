package org.vttish.gymcrmsystem.service;

import org.vttish.gymcrmsystem.dto.*;

import java.util.List;
import java.util.UUID;

public interface TrainerService {
    TrainerDto create(CreateTrainerDto createTrainerDto);
    TrainerDetailsDto getById(UUID id);
    List<TrainerCardDto> getAll();
    TrainerDetailsDto update(UpdateTrainerDto updateTrainerDto);
}
