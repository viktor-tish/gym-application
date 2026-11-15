package org.vttish.gymapplication.core.dto.trainee;

import java.time.LocalDate;
import java.util.List;
import org.vttish.gymapplication.core.dto.trainer.TrainerItemResponseDto;

public record TraineeProfileResponseDto(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    Boolean isActive,
    List<TrainerItemResponseDto> trainers
) {
}
