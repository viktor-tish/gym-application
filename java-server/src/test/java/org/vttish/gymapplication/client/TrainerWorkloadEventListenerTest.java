package org.vttish.gymapplication.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.event.TrainingCreatedEvent;
import org.vttish.gymapplication.event.TrainingDeletedEvent;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadEventListenerTest {

  @Mock
  private TrainerWorkloadClient trainerWorkloadClient;

  @InjectMocks
  private TrainerWorkloadEventListener eventListener;

  @Test
  void onTrainingRegistered_ShouldCallClient_WhenEventIsReceived() {
    TrainingCreatedEvent event = new TrainingCreatedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );

    assertDoesNotThrow(() -> eventListener.onTrainingRegistered(event));

    verify(trainerWorkloadClient).submitWorkload(event);
  }

  @Test
  void onTrainingRegistered_ShouldCatchException_WhenClientFails() {
    TrainingCreatedEvent event = new TrainingCreatedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );
    doThrow(new RuntimeException("Service down")).when(trainerWorkloadClient).submitWorkload(event);

    assertDoesNotThrow(() -> eventListener.onTrainingRegistered(event));

    verify(trainerWorkloadClient).submitWorkload(event);
  }

  @Test
  void onTrainingCancelled_ShouldCallClient_WhenEventIsReceived() {
    TrainingDeletedEvent event = new TrainingDeletedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );

    assertDoesNotThrow(() -> eventListener.onTrainingCancelled(event));

    verify(trainerWorkloadClient).cancelWorkload(event);
  }

  @Test
  void onTrainingCancelled_ShouldCatchException_WhenClientFails() {
    TrainingDeletedEvent event = new TrainingDeletedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );

    doThrow(new RuntimeException("Client failed"))
        .when(trainerWorkloadClient).cancelWorkload(event);

    assertDoesNotThrow(() -> eventListener.onTrainingCancelled(event));

    verify(trainerWorkloadClient).cancelWorkload(event);
  }
}
