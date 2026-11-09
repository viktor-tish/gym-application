package org.vttish.gymapplication.workloadreport.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Month;
import org.junit.jupiter.api.Test;

class MonthlyWorkloadTest {

  @Test
  void shouldHandleWorkloadBusinessLogic() {
    TrainerProfile trainerProfile = new TrainerProfile("firstName.lastName", "firstName", "lastName", true);
    MonthlyWorkload workload = new MonthlyWorkload(trainerProfile, 2026, Month.JUNE, 60);

    assertEquals(Month.JUNE, workload.getTrainingMonth());
    assertFalse(workload.isEmpty());
    assertTrue(workload.matches(2026, Month.JUNE));
    assertFalse(workload.matches(2025, Month.JUNE));

    workload.recordTraining(30);
    assertEquals(90, workload.getTrainingSummaryDuration());

    workload.cancelTraining(40);
    assertEquals(50, workload.getTrainingSummaryDuration());

    workload.detachTrainerProfile();

    workload.cancelTraining(50);
    assertTrue(workload.isEmpty());
  }
}
