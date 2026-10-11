package org.vttish.gymapplication.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record RegisterTraineeRequestDto(

    @NotBlank(message = "First name is mandatory")
    String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    String address
) {
}
