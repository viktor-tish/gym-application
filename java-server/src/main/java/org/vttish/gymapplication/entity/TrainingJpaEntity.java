package org.vttish.gymapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainings")
@Getter
@Setter
public class TrainingJpaEntity extends BaseJpaEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "trainee_id", nullable = false)
  private TraineeJpaEntity trainee;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "trainer_id", nullable = false)
  private TrainerJpaEntity trainer;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "type_id", nullable = false)
  private TrainingTypeJpaEntity type;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private Integer duration;
}
