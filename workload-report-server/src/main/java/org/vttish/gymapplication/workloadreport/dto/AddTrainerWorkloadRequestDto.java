package org.vttish.gymapplication.workloadreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record AddTrainerWorkloadRequestDto(

    @NotBlank(message = "Username is mandatory")
    String username,

    @NotBlank(message = "First name is mandatory")
    String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    @NotNull(message = "Active status is mandatory")
    Boolean isActive,

    @NotNull(message = "Training date is mandatory")
    LocalDate trainingDate,

    @NotNull(message = "Training duration is mandatory")
    @Positive(message = "Training duration must be positive")
    Integer trainingDuration,

    @NotNull(message = "Action type is mandatory")
    ActionType actionType
) {
}
