package wolox.training.models;

import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsConstants.INVALID_YEAR;
import static wolox.training.constants.PreconditionsConstants.PAGES_MUST_BE_POSITIVE_NUMBER;

import com.google.common.base.Preconditions;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor
@Getter
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NonNull
  private long id;
  private String genre;
  @Column(nullable = false)
  @NonNull
  private String author;
  @Column(nullable = false)
  @NonNull
  private String image;
  @Column(nullable = false)
  @NonNull
  private String title;
  @Column(nullable = false)
  @NonNull
  private String subtitle;
  @Column(nullable = false)
  @NonNull
  private String publisher;
  @Column(nullable = false)
  @NonNull
  private String year;
  @Column(nullable = false)
  @NonNull
  private Integer pages;
  @Column(nullable = false)
  @NonNull
  private String isbn;
  @ManyToMany(mappedBy = "books")
  @JsonIgnoreProperties(value = "books")
  private List<User> users = new ArrayList<>();

  public void setId(long id) {
    this.id = id;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public void setAuthor(String author) {
    Preconditions.checkArgument(!author.isEmpty(), CANNOT_BE_EMPTY, "Author");
    this.author = author;
  }

  public void setImage(String image) {
    Preconditions.checkArgument(!image.isEmpty(), CANNOT_BE_EMPTY, "Image");
    this.image = image;
  }

  public void setTitle(String title) {
    Preconditions.checkArgument(!title.isEmpty(), CANNOT_BE_EMPTY, "Title");
    this.title = title;
  }

  public void setSubtitle(String subtitle) {
    Preconditions.checkArgument(!subtitle.isEmpty(), CANNOT_BE_EMPTY, "Subtitle");
    this.subtitle = subtitle;
  }

  public void setPublisher(String publisher) {
    Preconditions.checkArgument(!publisher.isEmpty(), CANNOT_BE_EMPTY, "Publisher");
    this.publisher = publisher;
  }

  public void setYear(String year) {
    Preconditions.checkArgument(!year.isEmpty(), CANNOT_BE_EMPTY, "Year");
    Preconditions.checkArgument(Integer.parseInt(year) <= LocalDate.now().getYear() && Integer.parseInt(year) > 0, INVALID_YEAR);
    this.year = year;
  }

  public void setPages(Integer page) {
    Preconditions.checkArgument(page > 0, PAGES_MUST_BE_POSITIVE_NUMBER, Integer.toString(page));
    this.pages = page;
  }

  public void setIsbn(String isbn) {
    Preconditions.checkArgument(!isbn.isEmpty(), CANNOT_BE_EMPTY, "ISBN");
    this.isbn = isbn;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
