package org.vttish.gymapplication.event;

import java.time.LocalDate;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.entity.User;

public record TrainingDeletedEvent(
    String trainerUsername,
    String trainerFirstName,
    String trainerLastName,
    Boolean trainerIsActive,
    LocalDate date,
    Integer duration
) {

  public static TrainingDeletedEvent from(Trainer trainer, Training training) {
    User user = trainer.getUser();

    return new TrainingDeletedEvent(
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        user.getIsActive(),
        training.getDate(),
        training.getDuration()
    );
  }
}
