package org.vttish.gymcrmsystem.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

@Getter
@Setter
public class TrainerDto {
  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private boolean isActive;
  private UUID userId;
  private TrainingType specialization;
}
