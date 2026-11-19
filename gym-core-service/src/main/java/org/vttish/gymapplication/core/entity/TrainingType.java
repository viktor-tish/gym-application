package org.vttish.gymapplication.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "training_types")
@Getter
@Setter
public class TrainingType extends BaseEntity {

  @Column(nullable = false)
  private String name;
}
