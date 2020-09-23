package wolox.training.repositories;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  @Query("SELECT u FROM User u"
      + " WHERE (cast(:startDate as date) is null OR u.birthdate >= :startDate)"
      + " AND (cast(:endDate as date) is null OR u.birthdate <= :endDate)"
      + " AND (:name = '' OR UPPER(u.name) LIKE UPPER(CONCAT('%',:name,'%')))")
  Iterable<User> findAllByBirthdateBetweenAndNameContainingIgnoreCase(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("name") String name);

  @Query("SELECT u FROM User u"
      + " WHERE (cast(:birthdate as date) is null OR u.birthdate = :birthdate)"
      + " AND (:name is null OR UPPER(u.name) LIKE UPPER(CONCAT('%',:name,'%')))"
      + " AND (:username is null OR UPPER(u.username) LIKE UPPER(CONCAT('%',:username,'%')))")
  Iterable<User> findAllByFilters(
      @Param("birthdate") LocalDate birthdate,
      @Param("name") String name,
      @Param("username") String username);
}
