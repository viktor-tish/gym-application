package org.vttish.gymcrmsystem.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Trainer extends User {
  private final UUID userId;
  private TrainingType specialization;

  public Trainer() {
    userId = UUID.randomUUID();
  }
}
