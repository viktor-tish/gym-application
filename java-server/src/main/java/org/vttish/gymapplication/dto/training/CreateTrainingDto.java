package org.vttish.gymapplication.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTrainingDto {

  @NotBlank(message = "Trainee username is mandatory")
  private String traineeUsername;

  @NotBlank(message = "Trainer username is mandatory")
  private String trainerUsername;

  @NotBlank(message = "Training name is mandatory")
  private String name;

  @NotNull(message = "Date is mandatory")
  private LocalDate date;

  @NotNull(message = "Duration is mandatory")
  @Positive(message = "Duration must be positive")
  private Integer duration;
}
