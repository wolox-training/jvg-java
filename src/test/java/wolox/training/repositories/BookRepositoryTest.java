package wolox.training.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.BookFactory;
import wolox.training.models.Book;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BookRepository bookRepository;

  private Book book;
  private final BookFactory bookFactory = new BookFactory();

  @BeforeEach
  void beforeEachTest(){
    book = bookFactory.createTestBook();
  }

  @Test
  public void whenFindByAuthor_thenReturnBook(){
    entityManager.persist(book);
    entityManager.flush();

    Optional<Book> expectedBook = bookRepository.findByAuthor(book.getAuthor());

    assertThat(expectedBook).isPresent();
    assertThat(expectedBook.get().getId()).isPositive();
  }

  @Test
  public void whenFindByIdAndDoesNotExists_thenReturnEmpty(){
    Optional<Book> expectedBook = bookRepository.findById(1L);

    assertThat(expectedBook).isNotPresent();
  }

  @Test
  public void whenSaveBook_thenReturnBookSavedWithId(){
    Book bookSaved = bookRepository.save(book);

    assertThat(bookSaved.getId()).isPositive();
  }

  @Test
  public void whenDeleteById_thenReturnEmpty(){
    Book bookSaved = entityManager.persist(book);
    entityManager.flush();

    bookRepository.deleteById(bookSaved.getId());

    Optional<Book> expectedBook = bookRepository.findById(bookSaved.getId());

    assertThat(expectedBook).isNotPresent();
  }
}
