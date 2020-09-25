package wolox.training.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.BookFactory;
import wolox.training.UserFactory;
import wolox.training.exception.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.security.CustomAuthenticationProvider;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

  private User user;
  private String jsonUser;
  private Book book;
  private String jsonBook;
  private final BookFactory bookFactory = new BookFactory();
  private final UserFactory userFactory = new UserFactory();

  private static final String ADD_BOOK_PATH = "/api/users/%s/books/add";
  private static final String DELETE_BOOK_PATH = "/api/users/%s/books/delete";

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private BookController bookController;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private CustomAuthenticationProvider customAuthenticationProvider;

  @MockBean
  private PasswordEncoder passwordEncoder;


  @BeforeEach
  void beforeEachTest() throws JsonProcessingException {
    book = bookFactory.createTestBook();
    jsonBook = bookFactory.getJsonBook(book);
    user = userFactory.createTestUser();
    user.setUserId(1);
    jsonUser = userFactory.getJsonUser(user);
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenGetAllUsers_thenReturnUsersList() throws Exception {
    List<User> users = new ArrayList<>();
    users.add(user);

    given(userRepository.findAll()).willReturn(users);

    mvc.perform(get("/api/users")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenFindUserByIdAndUserDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(userRepository.findById(user.getUserId())).willReturn(Optional.empty());

    mvc.perform(get("/api/users/".concat(Long.toString(user.getUserId())))
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenFindUserByUsername_thenReturnUser() throws Exception {
    given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.ofNullable(user));

    mvc.perform(get("/api/users/username/".concat(user.getUsername()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @Test
  public void whenCreateUser_thenReturnUser() throws Exception {
    given(userRepository.save(any(User.class))).willReturn(user);

    mvc.perform(post("/api/users")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonUser))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @WithMockUser(value="anUser")
  @Test
  public void  whenDeleteUserAndDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(userRepository.findById(user.getUserId())).willReturn(Optional.empty());

    mvc.perform(delete("/api/users/".concat(Long.toString(user.getUserId())))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenUpdateUserAndIdMismatch_thenReturnIdMismatchException() throws Exception {
    mvc.perform(put("/api/users/2")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonUser))
        .andExpect(status().isPreconditionFailed());
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenAddBookToUser_thenReturnUserWithBook() throws Exception {
    book.setId(1);
    jsonBook = bookFactory.getJsonBook(book);
    given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
    given(bookController.findOne(book.getId())).willReturn(book);
    given(userRepository.save(any(User.class))).willReturn(user);
    String path = String.format(ADD_BOOK_PATH, user.getUserId());

    mvc.perform(put(path)
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonBook))
        .andExpect(jsonPath("$.books", hasSize(1)))
        .andExpect(jsonPath("$.books[0].id", is(1)));
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenDeleteABookFromUserAndBookDoesNotExists_thenReturnNotFoundException()
      throws Exception {
    book.setId(1);
    jsonBook = bookFactory.getJsonBook(book);
    given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
    given(bookController.findOne(book.getId())).willThrow(new BookNotFoundException(book.getId()));
    String path = String.format(DELETE_BOOK_PATH, user.getUserId());

    mvc.perform(delete(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenFindByIdWithNoAuth_thenReturnUnauthorizedException() throws Exception {
    mvc.perform(get("/api/users/".concat(Long.toString(user.getUserId())))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
