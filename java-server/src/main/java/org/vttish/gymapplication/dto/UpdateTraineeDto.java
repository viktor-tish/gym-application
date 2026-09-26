package org.vttish.gymapplication.dto;

import java.time.LocalDate;
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
public class UpdateTraineeDto {
  private UUID userId;
  private String firstName;
  private String lastName;
  private String username;

  @ToString.Exclude
  private String password;

  private boolean isActive;
  private LocalDate dateOfBirth;
  private String address;
}
