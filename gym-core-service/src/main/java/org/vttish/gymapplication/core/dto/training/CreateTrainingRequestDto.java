package org.vttish.gymapplication.core.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateTrainingRequestDto(

    @NotBlank(message = "Trainer username is mandatory")
    String trainerUsername,

    @NotBlank(message = "Trainee username is mandatory")
    String traineeUsername,

    @NotBlank(message = "Training name is mandatory")
    String name,

    @NotNull(message = "Date is mandatory")
    LocalDate date,

    @NotNull(message = "Duration is mandatory")
    @Positive(message = "Duration must be positive")
    Integer duration
) {
}
