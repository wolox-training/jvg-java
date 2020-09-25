package wolox.training.repositories;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Iterable<User> findAllByBirthdateBetweenAndNameContainingIgnoreCase(LocalDate startDate, LocalDate endDate, String name);

}
