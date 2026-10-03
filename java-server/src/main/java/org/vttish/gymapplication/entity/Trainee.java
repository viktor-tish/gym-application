package org.vttish.gymapplication.entity;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Trainee extends User {
  private final UUID userId;
  private LocalDate dateOfBirth;
  private String address;

  public Trainee() {
    userId = UUID.randomUUID();
  }
}
