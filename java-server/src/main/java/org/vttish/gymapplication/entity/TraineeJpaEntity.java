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
import org.hibernate.annotations.BatchSize;

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
  @BatchSize(size = 50)
  private Set<TrainerJpaEntity> trainers = new HashSet<>();

  public void addTrainer(TrainerJpaEntity trainer) {
    this.trainers.add(trainer);
    trainer.getTrainees().add(this);
  }

  public void removeTrainer(TrainerJpaEntity trainer) {
    this.trainers.remove(trainer);
    trainer.getTrainees().remove(this);
  }
}
