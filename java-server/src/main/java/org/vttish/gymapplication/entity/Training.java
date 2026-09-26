package org.vttish.gymapplication.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Training {
  private final UUID id;
  private UUID traineeId;
  private UUID trainerId;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private Duration trainingDuration;

  public Training() {
    id = UUID.randomUUID();
  }
}
