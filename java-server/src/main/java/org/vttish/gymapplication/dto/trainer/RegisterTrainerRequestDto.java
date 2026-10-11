package org.vttish.gymapplication.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterTrainerRequestDto(

    @NotBlank(message = "First name is mandatory")
    String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    @NotNull(message = "Specialization is mandatory")
    UUID specializationId
) {}
