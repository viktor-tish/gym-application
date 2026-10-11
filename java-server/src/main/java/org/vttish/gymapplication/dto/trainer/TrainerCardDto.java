package org.vttish.gymapplication.dto.trainer;

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
public class TrainerCardDto {
  private String firstName;
  private String lastName;
  private String username;
  private String specialization;
  private UUID specializationId;
}
