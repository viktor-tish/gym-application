package org.vttish.gymapplication.core.dto.trainee;

import java.time.LocalDate;

public record TraineeDetailsItemResponseDto(
    String username,
    String firstName,
    String lastName,
    Boolean isActive,
    LocalDate dateOfBirth
) {
}
