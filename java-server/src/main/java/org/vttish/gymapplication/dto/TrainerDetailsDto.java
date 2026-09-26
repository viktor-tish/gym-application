package org.vttish.gymapplication.dto;

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
public class TrainerDetailsDto {
  private String firstName;
  private String lastName;
  private String username;

  @ToString.Exclude
  private String password;

  private boolean isActive;
  private UUID userId;
  private TrainingType specialization;
}
