package org.vttish.gymapplication.report.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

@Entity
@Table(name = "trainer_profiles")
@Getter
public class TrainerProfile extends BaseEntity {

  @Column(nullable = false, unique = true, updatable = false)
  private String username;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private boolean isActive;

  @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<MonthlyWorkload> workloads = new HashSet<>();

  protected TrainerProfile() {
  }

  public TrainerProfile(String username, String firstName, String lastName, boolean isActive) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.isActive = isActive;
  }

  public Set<MonthlyWorkload> getWorkloads() {
    return Collections.unmodifiableSet(workloads);
  }

  public void syncProfile(String firstName, String lastName, boolean isActive) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.isActive = isActive;
  }

  public void recordTraining(LocalDate date, int duration) {
    findMonthlyWorkload(date).orElseGet(() -> createMonthlyWorkload(date)).recordTraining(duration);
  }

  public void cancelTraining(LocalDate date, int duration) {
    findMonthlyWorkload(date).ifPresent(
        workload -> {
          workload.cancelTraining(duration);

          if (workload.isEmpty()) {
            removeMonthlyWorkload(workload);
          }
        });
  }

  private Optional<MonthlyWorkload> findMonthlyWorkload(LocalDate date) {
    return workloads.stream()
        .filter(w -> w.matches(date.getYear(), date.getMonth()))
        .findFirst();
  }

  private MonthlyWorkload createMonthlyWorkload(LocalDate date) {
    MonthlyWorkload created = new MonthlyWorkload(this, date.getYear(), date.getMonth(), 0);
    workloads.add(created);

    return created;
  }

  private void removeMonthlyWorkload(MonthlyWorkload workload) {
    workloads.remove(workload);
    workload.detachTrainerProfile();
  }
}
