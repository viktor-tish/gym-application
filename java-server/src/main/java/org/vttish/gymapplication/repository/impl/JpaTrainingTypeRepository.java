package org.vttish.gymapplication.repository.impl;

import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.repository.TrainingTypeRepository;

@Repository
public class JpaTrainingTypeRepository extends JpaBaseRepository<TrainingType>
    implements TrainingTypeRepository {
  public JpaTrainingTypeRepository() {
    super(TrainingType.class);
  }
}
