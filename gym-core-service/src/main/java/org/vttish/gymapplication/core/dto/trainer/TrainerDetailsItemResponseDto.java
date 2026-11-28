package org.vttish.gymapplication.core.dto.trainer;

import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;

public record TrainerDetailsItemResponseDto(
    String username,
    String firstName,
    String lastName,
    TrainingTypeResponseDto specialization,
    Boolean isActive
) {
}
