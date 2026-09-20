package org.vttish.gymcrmsystem.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

@Getter
@Setter
public class TrainingDto {
  private UUID id;
  private UUID traineeId;
  private UUID trainerId;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private Duration trainingDuration;
}
