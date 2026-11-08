package org.vttish.gymapplication.workloadreport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Month;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "monthly_workloads")
@Getter
public class MonthlyWorkload extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "trainer_profile_id", nullable = false, updatable = false)
  private TrainerProfile trainerProfile;

  @Column(nullable = false, updatable = false)
  private int trainingYear;

  @Column(nullable = false, updatable = false)
  private int trainingMonth;

  @Setter
  @Column(nullable = false)
  private int trainingSummaryDuration;

  protected MonthlyWorkload() {
  }

  public MonthlyWorkload(
      TrainerProfile trainerProfile,
      int trainingYear,
      Month trainingMonth,
      int trainingDuration
  ) {
    this.trainerProfile = trainerProfile;
    this.trainingYear = trainingYear;
    this.trainingMonth = trainingMonth.getValue();
    this.trainingSummaryDuration = trainingDuration;
  }

  public Month getTrainingMonth() {
    return Month.of(trainingMonth);
  }

  boolean isEmpty() {
    return trainingSummaryDuration == 0;
  }

  void detachTrainerProfile() {
    trainerProfile = null;
  }

  boolean matches(int year, Month month) {
    return year == trainingYear && month.getValue() == trainingMonth;
  }

  void recordTraining(int duration) {
    trainingSummaryDuration += duration;
  }

  void cancelTraining(int duration) {
    trainingSummaryDuration -= Math.max(0, duration);
  }
}
