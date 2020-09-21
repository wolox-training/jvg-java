package wolox.training;

import java.time.LocalDate;
import wolox.training.models.User;

public class UserFactory {

  public UserFactory(){}

  public User createTestUser(){
    User user = new User();
    user.setName("testUser");
    user.setUsername("testUsername");
    user.setBirthdate(LocalDate.of(1996,11,25));
    user.setPassword("testPassword");
    return user;
  }

  public String getJsonUser(User user){
    return "{" +
        "\"name\": \"" + user.getName() + "\"," +
        "\"username\": \"" + user.getUsername() + "\"," +
        "\"birthdate\": \"" + user.getBirthdate() + "\"" +
        "}";
  }

}
