package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;

public record TrainerTrainingItemResponseDto(
    String name,
    LocalDate date,
    TrainingTypeResponseDto type,
    Integer duration,
    String traineeUsername
) {}
