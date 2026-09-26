package org.vttish.gymapplication.dto;

import java.time.LocalDate;
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
public class CreateTraineeDto {
  private String firstName;
  private String lastName;
  private boolean isActive;
  private LocalDate dateOfBirth;
  private String address;
}
