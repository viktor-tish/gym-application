package org.vttish.gymapplication.dto.trainer;

import java.util.UUID;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;

public record TrainerItemResponseDto(
    String username,
    String firstName,
    String lastName,
    TrainingTypeResponseDto specialization
) {}
