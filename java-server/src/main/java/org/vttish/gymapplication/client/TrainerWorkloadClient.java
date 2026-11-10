package org.vttish.gymapplication.client;

import org.vttish.gymapplication.event.TrainingCreatedEvent;
import org.vttish.gymapplication.event.TrainingDeletedEvent;

public interface TrainerWorkloadClient {

  void submitWorkload(TrainingCreatedEvent event);

  void cancelWorkload(TrainingDeletedEvent event);
}
