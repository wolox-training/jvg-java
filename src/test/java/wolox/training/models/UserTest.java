package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static wolox.training.constants.PreconditionsMessages.UNBORN;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class UserTest {

  private User user = new User();

  @Test
  public void whenSetBirthDateWithFutureDate_thenThrowsException(){
    LocalDate birthdate = LocalDate.now();
    birthdate.plusYears(1);
    Exception exception = assertThrows(Exception.class, () -> user.setBirthdate(birthdate));
    assertThat(exception.getMessage()).isEqualTo(String.format(UNBORN, birthdate.toString()));
  }

}
