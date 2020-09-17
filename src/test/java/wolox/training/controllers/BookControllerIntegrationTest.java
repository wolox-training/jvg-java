package wolox.training.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

  private Book book;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void whenFindBookById_ThenReturnAnExistingBook() throws Exception {
    Long testId = 1L;
    book = new Book();
    book.setId(testId);
    book.setTitle("Testing book");

    given(bookRepository.findById(testId)).willReturn(java.util.Optional.ofNullable(book));

    mvc.perform(get("/api/books/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("title").value(book.getTitle()));
  }

  @Test
  public void whenFindBookByAuthorAndDoesntExists_ThenReturnNotFoundException() throws Exception {
    given(bookRepository.findByAuthor("AuthorTest")).willReturn(Optional.empty());

    mvc.perform(get("/api/books/author/AuthorTest").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

}
