package org.vttish.gymapplication.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vttish.gymapplication.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  @Query("SELECT u.username FROM User u WHERE u.username LIKE CONCAT(:prefix, '%')")
  List<String> findUsernamesByPrefix(@Param("prefix") String prefix);
}
