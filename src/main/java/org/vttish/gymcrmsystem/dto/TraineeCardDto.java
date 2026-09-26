package org.vttish.gymcrmsystem.dto;

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
public class TraineeCardDto {
  private String firstName;
  private String lastName;
  private String username;
  private UUID userId;
}
