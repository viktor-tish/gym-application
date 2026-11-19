package org.vttish.gymapplication.report.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainerProfileTest {

  private TrainerProfile trainerProfile;

  @BeforeEach
  void setUp() {
    trainerProfile = new TrainerProfile("firstName.lastName", "firstName", "lastName", true);
  }

  @Test
  void shouldSyncProfileSuccessfully() {
    trainerProfile.syncProfile("newFirstName", "newLastName", false);

    assertEquals("newFirstName", trainerProfile.getFirstName());
    assertEquals("newLastName", trainerProfile.getLastName());
    assertFalse(trainerProfile.isActive());

    assertEquals("firstName.lastName", trainerProfile.getUsername());
  }

  @Test
  void shouldCreateNewMonthlyWorkloadWhenRecordingTrainingForNewMonth() {
    LocalDate date = LocalDate.of(2026, 6, 15);

    trainerProfile.recordTraining(date, 60);

    assertEquals(1, trainerProfile.getWorkloads().size());
    MonthlyWorkload workload = trainerProfile.getWorkloads().iterator().next();
    assertEquals(60, workload.getTrainingSummaryDuration());
  }

  @Test
  void shouldAccumulateDurationWhenRecordingTrainingInSameMonth() {
    LocalDate date1 = LocalDate.of(2026, 6, 10);
    LocalDate date2 = LocalDate.of(2026, 6, 25);

    trainerProfile.recordTraining(date1, 45);
    trainerProfile.recordTraining(date2, 30);

    assertEquals(1, trainerProfile.getWorkloads().size());
    MonthlyWorkload workload = trainerProfile.getWorkloads().iterator().next();
    assertEquals(75, workload.getTrainingSummaryDuration());
  }

  @Test
  void shouldReduceDurationWhenCancelingTrainingPartially() {
    LocalDate date = LocalDate.of(2026, 6, 10);
    trainerProfile.recordTraining(date, 90);

    trainerProfile.cancelTraining(date, 30);

    assertEquals(1, trainerProfile.getWorkloads().size());
    MonthlyWorkload workload = trainerProfile.getWorkloads().iterator().next();
    assertEquals(60, workload.getTrainingSummaryDuration());
  }

  @Test
  void shouldRemoveMonthlyWorkloadWhenCompletelyCanceled() {
    LocalDate date = LocalDate.of(2026, 6, 10);
    trainerProfile.recordTraining(date, 60);

    trainerProfile.cancelTraining(date, 60);

    assertTrue(trainerProfile.getWorkloads().isEmpty());
  }

  @Test
  void shouldNotFailWhenCancelingTrainingForNonExistentMonth() {
    LocalDate date = LocalDate.of(2026, 7, 10);

    trainerProfile.cancelTraining(date, 30);

    assertTrue(trainerProfile.getWorkloads().isEmpty());
  }
}
