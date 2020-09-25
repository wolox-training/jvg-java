package wolox.training.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.UserFactory;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomAuthenticationProviderTest {

  private User user;
  private Authentication authentication;
  private final UserFactory userFactory = new UserFactory();

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  CustomAuthenticationProvider customAuthenticationProvider;

  @Test
  public void whenAuthenticateWithValidUserMatchingPassword_thenReturnToken(){
    user = userFactory.createTestUser();
    authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>());
    given(userRepository.findByUsername(authentication.getName())).willReturn(Optional.ofNullable(user));
    given(passwordEncoder.matches(authentication.getCredentials().toString(),user.getPassword())).willReturn(true);

    Authentication authResponse = customAuthenticationProvider.authenticate(authentication);

    assertTrue(authResponse.isAuthenticated());
    verify(userRepository,times(1)).findByUsername(authentication.getName());
    verify(passwordEncoder,times(1)).matches(authentication.getCredentials().toString(), user.getPassword());
  }

  @Test
  public void whenAuthenticateWithValidUserNotMatchingPassword_thenReturnToken(){
    user = userFactory.createTestUser();
    authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>());
    given(userRepository.findByUsername(authentication.getName())).willReturn(Optional.ofNullable(user));
    given(passwordEncoder.matches(authentication.getCredentials().toString(),user.getPassword())).willReturn(false);

    assertThrows(BadCredentialsException.class,
        ()-> customAuthenticationProvider.authenticate(authentication));

    verify(userRepository,times(1)).findByUsername(authentication.getName());
    verify(passwordEncoder,times(1)).matches(authentication.getCredentials().toString(), user.getPassword());

  }

  @Test
  public void whenAuthenticateWithInvalidUser_thenReturnToken(){
    user = userFactory.createTestUser();
    authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>());
    given(userRepository.findByUsername(authentication.getName())).willReturn(Optional.empty());

    assertThrows(BadCredentialsException.class,
        ()-> customAuthenticationProvider.authenticate(authentication));

    verify(userRepository,times(1)).findByUsername(authentication.getName());

  }
}
