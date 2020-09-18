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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.BookFactory;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

  private Book book;
  private String jsonBook;
  private final BookFactory bookFactory = new BookFactory();

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private UserRepository userRepository;

  @BeforeEach
  void beforeEachTest(){
    book = bookFactory.createTestBook();
    book.setId(1);
    jsonBook = bookFactory.getJsonBook(book);
  }

  @Test
  public void whenFindAll_thenReturnBooksList() throws Exception {
    List<Book> books = new ArrayList<>();
    books.add(book);

    given(bookRepository.findAll()).willReturn(books);

    mvc.perform(get("/api/books/")
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].isbn", is(book.getIsbn())));
  }

  @Test
  public void whenFindBookById_thenReturnAnExistingBook() throws Exception {

    given(bookRepository.findById(book.getId())).willReturn(Optional.ofNullable(book));

    mvc.perform(get("/api/books/".concat(Long.toString(book.getId())))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
  }

  @Test
  public void whenFindBookByAuthorAndDoesntExists_thenReturnNotFoundException() throws Exception {
    String author = "unknown";
    given(bookRepository.findByAuthor(author)).willReturn(Optional.empty());

    mvc.perform(get("/api/books/author/".concat(author)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenCreateBook_thenReturnBookCreated() throws  Exception {
    given(bookRepository.save(any(Book.class))).willReturn(book);

    mvc.perform(post("/api/books/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isCreated());

  }

  @Test
  public void whenDeleteBookThatDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(bookRepository.findById(book.getId())).willReturn(Optional.empty());

    mvc.perform(delete("/api/books/".concat(Long.toString(book.getId())))
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenUpdateBookAndIdsDoesNotMatch_thenReturnMismatchException() throws Exception {
    mvc.perform(put("/api/books/2")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonBook))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void whenUpdateBook_thenReturnUpdatedBook() throws Exception {
    given(bookRepository.findById(book.getId())).willReturn(Optional.ofNullable(book));
    given(bookRepository.save(any(Book.class))).willReturn(book);
    mvc.perform(put("/api/books/".concat(Long.toString(book.getId())))
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
  }


}
