package org.vttish.gymapplication.core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.core.entity.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

  @Query("SELECT COUNT(t) > 0 FROM Trainer t WHERE t.user.username = :username")
  boolean existsByUsername(@Param("username") String username);

  @Query("SELECT t FROM Trainer t "
      + "JOIN FETCH t.user u "
      + "JOIN FETCH t.specialization s "
      + "WHERE u.username = :username")
  Optional<Trainer> findByUsername(@Param("username") String username);

  @Query("SELECT t FROM Trainer t "
      + "JOIN FETCH t.user u "
      + "JOIN FETCH t.specialization s "
      + "WHERE u.username IN :usernames")
  List<Trainer> findAllByUsernameIn(@Param("usernames") List<String> usernames);

  @Query("SELECT tr FROM Trainer tr "
      + "JOIN FETCH tr.user u "
      + "JOIN FETCH tr.specialization s "
      + "WHERE u.isActive = true AND tr NOT IN ("
      + " SELECT assignedTr FROM Trainee te "
      + " JOIN te.trainers assignedTr "
      + " WHERE te.user.username = :traineeUsername)")
  List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}
