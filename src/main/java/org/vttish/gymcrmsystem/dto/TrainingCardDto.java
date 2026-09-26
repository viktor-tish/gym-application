package org.vttish.gymcrmsystem.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.vttish.gymcrmsystem.entity.TrainingType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TrainingCardDto {
  private UUID id;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private Duration trainingDuration;
}
