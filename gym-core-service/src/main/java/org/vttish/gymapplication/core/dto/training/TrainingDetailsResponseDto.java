package org.vttish.gymapplication.core.dto.training;

import java.time.LocalDate;
import java.util.UUID;
import org.vttish.gymapplication.core.dto.trainee.TraineeDetailsItemResponseDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerDetailsItemResponseDto;

public record TrainingDetailsResponseDto(
    UUID id,
    String name,
    LocalDate date,
    TrainingTypeResponseDto type,
    Integer duration,
    TrainerDetailsItemResponseDto trainer,
    TraineeDetailsItemResponseDto trainee
) {
}
