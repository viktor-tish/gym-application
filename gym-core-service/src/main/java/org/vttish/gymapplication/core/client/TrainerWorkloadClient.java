package org.vttish.gymapplication.core.client;

import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;

public interface TrainerWorkloadClient {

  void submitWorkload(TrainingCreatedEvent event);

  void cancelWorkload(TrainingDeletedEvent event);
}
