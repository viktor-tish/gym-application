package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;
import java.util.UUID;

public record TraineeTrainingItemResponseDto(
    UUID id,
    String name,
    LocalDate date,
    TrainingTypeResponseDto type,
    Integer duration,
    String trainerUsername
) {
}
