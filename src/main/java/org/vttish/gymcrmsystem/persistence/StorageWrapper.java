package org.vttish.gymcrmsystem.persistence;

import org.vttish.gymcrmsystem.dto.TraineeDto;
import org.vttish.gymcrmsystem.dto.TrainerDto;
import org.vttish.gymcrmsystem.dto.TrainingDto;

import java.util.Map;
import java.util.UUID;

public record StorageWrapper(
        Map<UUID, TraineeDto> trainees,
        Map<UUID, TrainerDto> trainers,
        Map<UUID, TrainingDto> trainings
) {}
