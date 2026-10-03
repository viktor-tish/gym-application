package org.vttish.gymapplication.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.vttish.gymapplication.entity.TrainingType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTrainingDto {
  private UUID traineeId;
  private UUID trainerId;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private Duration trainingDuration;
}
