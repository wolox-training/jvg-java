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

  @Query("SELECT b FROM Book b"
      + " WHERE (:genre = '' OR UPPER(b.genre) LIKE UPPER(CONCAT('%',:genre,'%')))"
      + " AND (:author = '' OR UPPER(b.author) LIKE UPPER(CONCAT('%',:author,'%')))"
      + " AND (:image = '' OR UPPER(b.image) LIKE UPPER(CONCAT('%',:image,'%')))"
      + " AND (:title = '' OR UPPER(b.title) LIKE UPPER(CONCAT('%',:title,'%')))"
      + " AND (:subtitle = '' OR UPPER(b.subtitle) LIKE UPPER(CONCAT('%',:subtitle,'%')))"
      + " AND (:publisher = '' OR UPPER(b.publisher) LIKE UPPER(CONCAT('%',:publisher,'%')))"
      + " AND (:year = '' OR UPPER(b.year) LIKE UPPER(CONCAT('%',:year,'%')))"
      + " AND (:pages is null OR b.pages = :pages)"
      + " AND (:isbn = '' OR UPPER(b.isbn) LIKE UPPER(CONCAT('%',:isbn,'%')))")
  Iterable<Book> findAllByFilters(
      @Param("genre") String genre,
      @Param("author") String author,
      @Param("image") String image,
      @Param("title") String title,
      @Param("subtitle") String subtitle,
      @Param("publisher") String publisher,
      @Param("year") String year,
      @Param("pages") Integer pages,
      @Param("isbn") String isbn);
}
