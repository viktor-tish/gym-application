package org.vttish.gymapplication.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.TraineeJpaEntity;
import org.vttish.gymapplication.repository.TraineeRepository;

@Repository
public class JpaTraineeRepository extends JpaBaseRepository<TraineeJpaEntity>
    implements TraineeRepository {

  public JpaTraineeRepository() {
    super(TraineeJpaEntity.class);
  }

  @SuppressWarnings("java:S2077")
  @Override
  public List<TraineeJpaEntity> findAll() {
    String query = String.format(
        "SELECT t FROM %s t JOIN FETCH t.user u",
        entityClass.getSimpleName()
    );

    return entityManager.createQuery(query, entityClass).getResultList();
  }

  @SuppressWarnings("java:S2077")
  @Override
  public Optional<TraineeJpaEntity> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }

    String query = String.format(
        "SELECT t FROM %s t JOIN FETCH t.user u WHERE u.username = :username",
        entityClass.getSimpleName()
    );
                  
    List<TraineeJpaEntity> results = entityManager.createQuery(query, entityClass)
        .setParameter("username", username)
        .getResultList();
        
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public void deleteByUsername(String username) {
    if (username == null) {
      return;
    }
    findByUsername(username).ifPresent(entity -> entityManager.remove(entity));
  }
}
