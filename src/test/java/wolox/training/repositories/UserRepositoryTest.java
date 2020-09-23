package wolox.training.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.UserFactory;
import wolox.training.models.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  private User user;
  private final UserFactory userFactory = new UserFactory();

  @BeforeEach
  void beforeEachTest(){
    user = userFactory.createTestUser();
  }

  @Test
  public void whenFindByUsernameWhenDoesNotExists_thenReturnEmpty(){
    entityManager.persist(user);
    entityManager.flush();

    Optional<User> userFound = userRepository.findByUsername(user.getUsername().concat("empty"));

    assertThat(userFound).isNotPresent();
  }

  @Test
  public void whenFindByUsername_thenReturnUser(){
    entityManager.persist(user);
    entityManager.flush();

    Optional<User> userFound = userRepository.findByUsername(user.getUsername());

    assertThat(userFound).isPresent();
    assertThat(userFound.get().getName()).isEqualTo(user.getName());
  }

  @Test
  public void whenFindAllByBirthdateBetweenAndNameContainingIgnoreCase_thenReturnUsers(){
    entityManager.persist(user);

    Iterable<User> users = userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
        user.getBirthdate(), user.getBirthdate(), user.getName());

    assertThat(users.iterator().next().getName()).isEqualTo(user.getName());
  }

  @Test
  public void whenFindAllByBirthdateBetweenAndNameContainingIgnoreCase_thenReturnEmptyList(){

    Iterable<User> users = userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
        user.getBirthdate(), user.getBirthdate(), "NotAName");

    assertThat(users.iterator().hasNext()).isFalse();
  }

  @Test
  public void whenFindAllByBirthdateBetweenAndNameContainingIgnoreCaseWithNullDate_thenReturnUsers(){
    entityManager.persist(user);

    Iterable<User> users = userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
        user.getBirthdate(), null, user.getName());

    assertThat(users.iterator().next().getName()).isEqualTo(user.getName());
  }

  @Test
  public void whenFindAllByBirthdateBetweenAndNameContainingIgnoreCaseWithEmptyName_thenReturnUsers(){
    entityManager.persist(user);

    Iterable<User> users = userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
        user.getBirthdate(), null, "");

    assertThat(users.iterator().next().getName()).isEqualTo(user.getName());
  }

}
