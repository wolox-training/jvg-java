package wolox.training.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private Pageable pageable;

  @BeforeEach
  void beforeEachTest(){
    book = bookFactory.createTestBook();
    pageable = PageRequest.of(0,20);
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

  @Test
  public void whenFindAllByPublisherAndGenreAndYear_thenReturnABook(){
    entityManager.persist(book);

    Page<Book> books = bookRepository.findAllByPublisherAndGenreAndYear(book.getPublisher(), book.getGenre(), book.getYear(), pageable);

    assertThat(books.getContent().iterator().next().getAuthor()).isEqualTo(book.getAuthor());

  }

  @Test

  public void whenFindAllByPublisherAndGenreAndYear_thenReturnEmpty(){
    Page<Book> books = bookRepository.findAllByPublisherAndGenreAndYear(book.getPublisher(), book.getGenre(), "1", pageable);

    assertThat(books.getContent().size()).isEqualTo(0);
  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYearWithNullYear_thenReturnABook(){
    entityManager.persist(book);

    Page<Book> books = bookRepository.findAllByPublisherAndGenreAndYear(book.getPublisher(), book.getGenre(), null, pageable);

    assertThat(books.getContent().iterator().next().getAuthor()).isEqualTo(book.getAuthor());

  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYearWithNullGenderAndPublisher_thenReturnABook(){
    entityManager.persist(book);

    Page<Book> books = bookRepository.findAllByPublisherAndGenreAndYear(null, null, book.getYear(), pageable);

    assertThat(books.getContent().iterator().next().getAuthor()).isEqualTo(book.getAuthor());

  }

  @Test
  public void whenFindAllByFiltersWithYear_thenReturnABook(){
    entityManager.persist(book);

    Page<Book> books = bookRepository.findAllByFilters("","","","","","",book.getYear(),null,"", pageable);

    assertThat(books.getContent().iterator().next().getAuthor()).isEqualTo(book.getAuthor());

  }

  @Test
  public void whenFindAllByFiltersWithPages_thenReturnABook(){
    entityManager.persist(book);

    Page<Book> books = bookRepository.findAllByFilters("","","","","","","",book.getPages(),"", pageable);

    assertThat(books.getContent().iterator().next().getAuthor()).isEqualTo(book.getAuthor());

  }


}
