package org.vttish.gymapplication.core.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.core.entity.TrainingType;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, UUID> {
}
