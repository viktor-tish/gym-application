package org.vttish.gymapplication.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraineeDto {
  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private boolean isActive;
  private UUID userId;
  private LocalDate dateOfBirth;
  private String address;
}
