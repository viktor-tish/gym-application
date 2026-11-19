package org.vttish.gymapplication.core.event;

import java.time.LocalDate;
import org.vttish.gymapplication.core.entity.Trainer;
import org.vttish.gymapplication.core.entity.Training;
import org.vttish.gymapplication.core.entity.User;

public record TrainingCreatedEvent(
    String trainerUsername,
    String trainerFirstName,
    String trainerLastName,
    Boolean trainerIsActive,
    LocalDate date,
    Integer duration
) {

  public static TrainingCreatedEvent from(Trainer trainer, Training training) {
    User user = trainer.getUser();

    return new TrainingCreatedEvent(
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        user.getIsActive(),
        training.getDate(),
        training.getDuration()
    );
  }
}
