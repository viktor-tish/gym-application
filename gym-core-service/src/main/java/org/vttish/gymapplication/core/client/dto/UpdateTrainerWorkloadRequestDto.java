package org.vttish.gymapplication.core.client.dto;

import java.time.LocalDate;

public record UpdateTrainerWorkloadRequestDto(
    String username,
    String firstName,
    String lastName,
    Boolean isActive,
    LocalDate trainingDate,
    Integer trainingDuration,
    ActionType actionType
) {
}
