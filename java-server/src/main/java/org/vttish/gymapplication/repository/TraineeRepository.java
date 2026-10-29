package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.Trainee;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, UUID> {

  @Query("SELECT COUNT(t) > 0 FROM Trainee t WHERE t.user.username = :username")
  boolean existsByUsername(@Param("username") String username);

  @Query("SELECT t FROM Trainee t JOIN FETCH t.user u WHERE u.username = :username")
  Optional<Trainee> findByUsername(@Param("username") String username);

  default void deleteByUsername(String username) {
    findByUsername(username).ifPresent(this::delete);
  }
}
