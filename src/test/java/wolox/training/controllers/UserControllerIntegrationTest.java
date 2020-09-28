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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.BookFactory;
import wolox.training.UserFactory;
import wolox.training.exception.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.security.CustomAuthenticationProvider;

@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

  private User user;
  private String jsonUser;
  private Book book;
  private String jsonBook;
  private Pageable pageable;
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
    pageable = PageRequest.of(0,20);
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenGetAllUsers_thenReturnUsersList() throws Exception {
    List<User> users = new ArrayList<>();
    users.add(user);
    Page<User> usersPage = new PageImpl<>(users);

    given(userRepository.findAllByFilters(null,"","",pageable)).willReturn(usersPage);

    mvc.perform(get("/api/users")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].username").value(user.getUsername()));
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

  @WithMockUser(value = "anUser")
  @Test
  public void whenFindUserByBirthdateAndName_thenReturnUsers() throws Exception {
    List<User> users = new ArrayList<>();
    users.add(user);
    Page<User> usersPage = new PageImpl<>(users);

    given(userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
          user.getBirthdate().minusDays(2),
          user.getBirthdate().plusDays(2),
          user.getName(),
          pageable))
        .willReturn(usersPage);

    mvc.perform(get("/api/users/find")
    .contentType(MediaType.APPLICATION_JSON)
    .queryParam("startDateStr", user.getBirthdate().minusDays(2).toString())
    .queryParam("endDateStr", user.getBirthdate().plusDays(2).toString())
    .queryParam("name", user.getName()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].name").value(user.getName()));
  }

  @WithMockUser(value = "anUser")
  @Test
  public void whenFinduserByBirthdateAndName_thenReturnEmptyList() throws Exception {
    List<User> users = new ArrayList<>();
    Page<User> usersPage = new PageImpl<>(users);

    given(userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
          user.getBirthdate(),
          user.getBirthdate(),
          user.getName(),
          pageable))
        .willReturn(usersPage);

    mvc.perform(get("/api/users/find")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("startDateStr", user.getBirthdate().toString())
        .queryParam("endDateStr", user.getBirthdate().toString())
        .queryParam("name", user.getName()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenGetAllUsersByFilters_thenReturnUsersList() throws Exception {
    List<User> users = new ArrayList<>();
    users.add(user);
    Page<User> usersPage = new PageImpl<>(users);

    given(userRepository.findAllByFilters(user.getBirthdate(),user.getName(),user.getUsername(), pageable))
        .willReturn(usersPage);

    mvc.perform(get("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("birthdateStr",user.getBirthdate().toString())
        .queryParam("name",user.getName())
        .queryParam("username",user.getUsername()))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].username").value(user.getUsername()));
  }
}
