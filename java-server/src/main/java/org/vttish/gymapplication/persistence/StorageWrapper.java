package org.vttish.gymapplication.persistence;

import java.util.Map;
import java.util.UUID;
import org.vttish.gymapplication.dto.TraineeDto;
import org.vttish.gymapplication.dto.TrainerDto;
import org.vttish.gymapplication.dto.TrainingDto;

public record StorageWrapper(
    Map<UUID, TraineeDto> trainees,
    Map<UUID, TrainerDto> trainers,
    Map<UUID, TrainingDto> trainings
) {
}
