package org.vttish.gymapplication.core.dto.trainer;

import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;

public record TrainerItemResponseDto(
    String username,
    String firstName,
    String lastName,
    TrainingTypeResponseDto specialization
) {
}
