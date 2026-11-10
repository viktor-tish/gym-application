package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;
import java.util.UUID;

public record TrainerTrainingItemResponseDto(
    UUID id,
    String name,
    LocalDate date,
    TrainingTypeResponseDto type,
    Integer duration,
    String traineeUsername
) {
}
