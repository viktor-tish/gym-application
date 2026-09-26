package org.vttish.gymcrmsystem.dto;

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
public class UpdateTrainerDto {
  private UUID userId;
  private String firstName;
  private String lastName;
  private String username;

  @ToString.Exclude
  private String password;

  private boolean isActive;
  private TrainingType specialization;
}
