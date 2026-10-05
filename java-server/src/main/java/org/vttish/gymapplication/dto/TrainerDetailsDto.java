package org.vttish.gymapplication.dto;

import java.util.UUID;
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
public class TrainerDetailsDto {
  private String firstName;
  private String lastName;
  private String username;
  private boolean isActive;
  private String specialization;
  private UUID specializationId;
}
