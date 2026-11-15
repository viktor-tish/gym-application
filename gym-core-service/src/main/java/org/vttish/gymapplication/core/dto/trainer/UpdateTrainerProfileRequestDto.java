package org.vttish.gymapplication.core.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTrainerProfileRequestDto(

    @NotBlank(message = "First name is mandatory")
    String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    @NotNull(message = "Active status is mandatory")
    Boolean isActive
) {
}
