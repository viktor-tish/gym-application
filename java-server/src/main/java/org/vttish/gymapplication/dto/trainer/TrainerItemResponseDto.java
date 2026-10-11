package org.vttish.gymapplication.dto.trainer;

import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;

public record TrainerItemResponseDto(
    String username,
    String firstName,
    String lastName,
    TrainingTypeResponseDto specialization
) {
}
