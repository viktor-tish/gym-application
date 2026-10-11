package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;

public record TraineeTrainingItemResponseDto(
    String name,
    LocalDate date,
    TrainingTypeResponseDto type,
    Integer duration,
    String trainerUsername
) {}
