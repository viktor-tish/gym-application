package org.vttish.gymapplication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainees")
@Getter
@Setter
public class TraineeJpaEntity extends BaseJpaEntity {

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserJpaEntity user;

  private LocalDate dateOfBirth;
  private String address;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "trainee_trainers",
      joinColumns = @JoinColumn(name = "trainee_id"),
      inverseJoinColumns = @JoinColumn(name = "trainer_id")
  )
  private Set<TrainerJpaEntity> trainers = new HashSet<>();
}
