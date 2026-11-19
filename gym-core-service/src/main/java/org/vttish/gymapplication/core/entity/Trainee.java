package org.vttish.gymapplication.core.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
public class Trainee extends BaseEntity {

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private LocalDate dateOfBirth;
  private String address;

  @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<Training> trainings = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "trainee_trainers",
      joinColumns = @JoinColumn(name = "trainee_id"),
      inverseJoinColumns = @JoinColumn(name = "trainer_id")
  )
  @BatchSize(size = 50)
  private Set<Trainer> trainers = new HashSet<>();

  public void addTrainer(Trainer trainer) {
    this.trainers.add(trainer);
    trainer.getTrainees().add(this);
  }

  public void removeTrainer(Trainer trainer) {
    this.trainers.remove(trainer);
    trainer.getTrainees().remove(this);
  }
}
