package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class TrainingDetailsDto {
  private String name;
  private LocalDate date;
  private String type;
  private UUID typeId;
  private Integer duration;
  private String traineeUsername;
  private String trainerUsername;
}
