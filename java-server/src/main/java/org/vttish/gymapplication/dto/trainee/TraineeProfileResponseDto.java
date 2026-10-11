package org.vttish.gymapplication.dto.trainee;

import java.time.LocalDate;
import java.util.List;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;

public record TraineeProfileResponseDto(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    Boolean isActive,
    List<TrainerItemResponseDto> trainers
) {}
