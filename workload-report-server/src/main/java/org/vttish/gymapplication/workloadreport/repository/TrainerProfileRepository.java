package org.vttish.gymapplication.workloadreport.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.workloadreport.entity.TrainerProfile;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<TrainerProfile> findAndLockByUsername(String username);

  @EntityGraph(attributePaths = {"workloads"})
  Optional<TrainerProfile> findWithWorkloadsByUsername(String username);
}
