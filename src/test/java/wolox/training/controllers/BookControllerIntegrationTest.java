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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.BookFactory;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.security.CustomAuthenticationProvider;
import wolox.training.service.OpenLibraryService;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

  private Book book;
  private String jsonBook;
  private final BookFactory bookFactory = new BookFactory();
  private Pageable pageable;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private OpenLibraryService openLibraryService;

  @MockBean
  private CustomAuthenticationProvider customAuthenticationProvider;

  @BeforeEach
  void beforeEachTest() throws JsonProcessingException {
    book = bookFactory.createTestBook();
    book.setId(1);
    jsonBook = bookFactory.getJsonBook(book);
    pageable = PageRequest.of(0,20);
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenFindAll_thenReturnBooksList() throws Exception {
    List<Book> books = new ArrayList<>();
    books.add(book);
    Page<Book> booksPage = new PageImpl<>(books);

    given(bookRepository.findAllByFilters("","","","","","","",null,"", pageable))
        .willReturn(booksPage);

    mvc.perform(get("/api/books")
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].isbn").value(book.getIsbn()));
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenFindBookById_thenReturnAnExistingBook() throws Exception {

    given(bookRepository.findById(book.getId())).willReturn(Optional.ofNullable(book));

    mvc.perform(get("/api/books/".concat(Long.toString(book.getId())))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
  }

  @WithMockUser(value="anUser")
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

    mvc.perform(post("/api/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isCreated());

  }

  @WithMockUser(value="anUser")
  @Test
  public void whenDeleteBookThatDoesNotExists_thenReturnNotFoundException() throws Exception {
    given(bookRepository.findById(book.getId())).willReturn(Optional.empty());

    mvc.perform(delete("/api/books/".concat(Long.toString(book.getId())))
    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenUpdateBookAndIdsDoesNotMatch_thenReturnMismatchException() throws Exception {
    mvc.perform(put("/api/books/2")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonBook))
        .andExpect(status().isUnprocessableEntity());
  }

  @WithMockUser(value="anUser")
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

  @Test
  public void whenGetAllBookWithoutAuth_thenReturnUnauthorizedException() throws Exception {
    mvc.perform(get("/api/books")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @WithMockUser(value = "anUser")
  @Test
  public void whenFindBooksByQueryParams_thenReturnBooks() throws Exception {
    List<Book> books = new ArrayList<>();
    books.add(book);
    Page<Book> booksPage = new PageImpl<>(books);
    given(bookRepository.findAllByPublisherAndGenreAndYear(book.getPublisher(), book.getGenre(), book.getYear(), pageable))
        .willReturn(booksPage);

    mvc.perform(get("/api/books/find")
    .contentType(MediaType.APPLICATION_JSON)
    .queryParam("publisher", book.getPublisher())
    .queryParam("genre", book.getGenre())
    .queryParam("year", book.getYear()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].year").value(book.getYear()));
  }
  
  @WithMockUser(value = "anUser")
  @Test
  public void whenFindBooksByQueryParams_thenReturnEmptyList() throws Exception {
    List<Book> books = new ArrayList<>();
    Page<Book> booksPage = new PageImpl<>(books);
    given(bookRepository.findAllByPublisherAndGenreAndYear(book.getPublisher(), book.getGenre(), book.getYear(), pageable))
        .willReturn(booksPage);

    mvc.perform(get("/api/books/find")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher", book.getPublisher())
        .queryParam("genre", book.getGenre())
        .queryParam("year", book.getYear()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @WithMockUser(value="anUser")
  @Test
  public void whenFindAllByFilters_thenReturnBooksList() throws Exception {
    List<Book> books = new ArrayList<>();
    books.add(book);
    Page<Book> booksPage = new PageImpl<>(books);

    given(bookRepository.findAllByFilters(book.getGenre(),book.getAuthor(),book.getImage(),book.getTitle(),
        book.getSubtitle(),book.getPublisher(),book.getYear(),book.getPages(),book.getIsbn(), pageable))
        .willReturn(booksPage);

    mvc.perform(get("/api/books")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("genre",book.getGenre())
        .queryParam("author", book.getAuthor())
        .queryParam("image",book.getImage())
        .queryParam("title",book.getTitle())
        .queryParam("subtitle",book.getSubtitle())
        .queryParam("publisher",book.getPublisher())
        .queryParam("year",book.getYear())
        .queryParam("pages",book.getPages().toString())
        .queryParam("isbn",book.getIsbn()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(1))
      .andExpect(jsonPath("$.content[0].isbn", is(book.getIsbn())));
  }
}
