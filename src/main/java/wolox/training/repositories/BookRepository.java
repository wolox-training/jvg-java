package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wolox.training.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByAuthor(String author);

  Optional<Book> findByIsbn(String isbn);

  @Query("SELECT b from Book b "
      + " WHERE (:publisher is null OR b.publisher = :publisher)"
      + " AND (:genre is null OR b.genre = :genre)"
      + " AND (:year is null OR b.year = :year)")
  List<Book> findAllByPublisherAndGenreAndYear(
      @Param("publisher") String publisher,
      @Param("genre") String genre,
      @Param("year") String year);
}
