package org.vttish.gymapplication.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.repository.TrainingTypeRepository;

@Repository
public class JpaTrainingTypeRepository extends JpaBaseRepository<TrainingType>
    implements TrainingTypeRepository {
  public JpaTrainingTypeRepository() {
    super(TrainingType.class);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public Optional<TrainingType> findByName(String name) {
    if (name == null) {
      return Optional.empty();
    }

    String query = String.format(
        "SELECT t FROM %s t WHERE t.name = :name",
        entityClass.getSimpleName()
    );

    List<TrainingType> results = entityManager.createQuery(query, entityClass)
        .setParameter("name", name)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
