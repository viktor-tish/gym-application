package org.vttish.gymapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseJpaEntity {

  @Id
  private UUID id = UUID.randomUUID();

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof BaseJpaEntity other)) {
      return false;
    }

    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
