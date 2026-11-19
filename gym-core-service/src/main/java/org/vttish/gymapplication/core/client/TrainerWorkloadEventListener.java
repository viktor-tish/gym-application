package org.vttish.gymapplication.core.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadEventListener {

  private final TrainerWorkloadClient trainerWorkloadClient;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onTrainingRegistered(TrainingCreatedEvent event) {
    safetyWrapper(() -> trainerWorkloadClient.submitWorkload(event), event.trainerUsername());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onTrainingCancelled(TrainingDeletedEvent event) {
    safetyWrapper(() -> trainerWorkloadClient.cancelWorkload(event), event.trainerUsername());
  }

  private void safetyWrapper(Runnable runnable, String username) {
    try {
      runnable.run();
    } catch (RuntimeException exception) {
      log.error("Failed to report workload for trainer {}", username, exception);
    }
  }
}
