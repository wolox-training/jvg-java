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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.exception.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

  private User user;
  private String jsonUser;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private BookController bookController;

  @MockBean
  private BookRepository bookRepository;

  @BeforeEach
  public void beforeEachTest(){
    user = new User();
    user.setName("testUser");
    user.setUsername("testUsername");
    user.setUserId(1);
    user.setBirthdate(LocalDate.of(1996,11,25));
    jsonUser = "{" +
        "\"name\": \"" + user.getName() + "\"," +
        "\"username\": \"" + user.getUsername() + "\"," +
        "\"birthdate\": \"" + user.getBirthdate() + "\"" +
        "}";

  }

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

  @Test
  public void whenFindUserByIdAndUserDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(userRepository.findById(user.getUserId())).willReturn(Optional.empty());

    mvc.perform(get("/api/users/".concat(Long.toString(user.getUserId())))
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

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

    mvc.perform(post("/api/users/")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonUser))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @Test
  public void  whenDeleteUserAndDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(userRepository.findById(user.getUserId())).willReturn(Optional.empty());

    mvc.perform(delete("/api/users/".concat(Long.toString(user.getUserId())))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenUpdateUserAndIdMismatch_thenReturnIdMismatchException() throws Exception {
    mvc.perform(put("/api/users/2")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonUser))
        .andExpect(status().isPreconditionFailed());
  }

  @Test
  public void whenAddBookToUser_thenReturnUserWithBook() throws Exception {
    User userWithBook = user;
    Book book = new Book();
    book.setAuthor("Author");
    book.setImage("Image");
    book.setTitle("Title");
    book.setSubtitle("Subtitle");
    book.setPublisher("Publisher");
    book.setYear("Year");
    book.setPages(20);
    book.setIsbn("ISBN");
    book.setGenre("Genre");
    book.setId(1);
    String jsonBook = "{\"genre\": \"" + book.getGenre() + "\"," +
        "\"author\": \"" + book.getAuthor() + "\"," +
        "\"image\": \"" + book.getImage() + "\"," +
        "\"title\": \"" + book.getTitle() + "\"," +
        "\"subtitle\": \"" + book.getSubtitle() + "\"," +
        "\"publisher\": \"" + book.getPublisher() + "\"," +
        "\"year\": \"" + book.getYear() + "\"," +
        "\"pages\": \"" + book.getPages() + "\"," +
        "\"isbn\": \"" + book.getIsbn() + "\"," +
        "\"id\": \"" + book.getId() + "\"" +
        "}";

    given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
    given(bookController.findOne(book.getId())).willReturn(book);
    given(userRepository.save(any(User.class))).willReturn(userWithBook);

    mvc.perform(put("/api/users/".concat(Long.toString(user.getUserId())).concat("/books/add"))
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonBook))
        .andExpect(jsonPath("$.books", hasSize(1)))
        .andExpect(jsonPath("$.books[0].id", is(1)));
  }

  @Test
  public void whenDeleteABookFromUserAndBookDoesNotExists_thenReturnNotFoundException()
      throws Exception {
    Book book = new Book();
    book.setAuthor("Author");
    book.setImage("Image");
    book.setTitle("Title");
    book.setSubtitle("Subtitle");
    book.setPublisher("Publisher");
    book.setYear("Year");
    book.setPages(20);
    book.setIsbn("ISBN");
    book.setGenre("Genre");
    book.setId(1);
    String jsonBook = "{\"genre\": \"" + book.getGenre() + "\"," +
        "\"author\": \"" + book.getAuthor() + "\"," +
        "\"image\": \"" + book.getImage() + "\"," +
        "\"title\": \"" + book.getTitle() + "\"," +
        "\"subtitle\": \"" + book.getSubtitle() + "\"," +
        "\"publisher\": \"" + book.getPublisher() + "\"," +
        "\"year\": \"" + book.getYear() + "\"," +
        "\"pages\": \"" + book.getPages() + "\"," +
        "\"isbn\": \"" + book.getIsbn() + "\"," +
        "\"id\": \"" + book.getId() + "\"" +
        "}";
    given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
    given(bookController.findOne(book.getId())).willThrow(new BookNotFoundException(book.getId()));

    mvc.perform(delete("/api/users/".concat(Long.toString(user.getUserId())).concat("/books/delete"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }
}
