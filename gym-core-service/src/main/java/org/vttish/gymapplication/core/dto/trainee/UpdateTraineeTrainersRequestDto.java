package org.vttish.gymapplication.core.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateTraineeTrainersRequestDto(

    @NotEmpty(message = "Trainers username list is mandatory")
    List<@NotBlank(message = "Username cannot be blank") String> trainers
) {
}
