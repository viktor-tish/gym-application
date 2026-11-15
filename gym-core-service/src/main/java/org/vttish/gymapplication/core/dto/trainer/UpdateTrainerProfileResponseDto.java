package org.vttish.gymapplication.core.dto.trainer;

import java.util.List;
import org.vttish.gymapplication.core.dto.trainee.TraineeItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;

public record UpdateTrainerProfileResponseDto(
    String username,
    String firstName,
    String lastName,
    TrainingTypeResponseDto specialization,
    Boolean isActive,
    List<TraineeItemResponseDto> trainees
) {
}
