package wolox.training.models;

import static wolox.training.constants.PreconditionsMessages.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsMessages.CANNOT_BE_NULL;
import static wolox.training.constants.PreconditionsMessages.PAGES_MUST_BE_POSITIVE_NUMBER;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String genre;
  @Column(nullable = false)
  private String author;
  @Column(nullable = false)
  private String image;
  @Column(nullable = false)
  private String title;
  @Column(nullable = false)
  private String subtitle;
  @Column(nullable = false)
  private String publisher;
  @Column(nullable = false)
  private String year;
  @Column(nullable = false)
  private Integer page;
  @Column(nullable = false)
  private String isbn;
  @Column(nullable = false)
  @ManyToMany()
  private List<User> users = new ArrayList<>();

  public Book() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    Preconditions.checkNotNull(id, CANNOT_BE_NULL, "Book ID");
    this.id = id;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    Preconditions.checkNotNull(genre, CANNOT_BE_NULL, "Genre");
    Preconditions.checkArgument(!genre.isEmpty(), CANNOT_BE_EMPTY, "Genre");
    this.genre = genre;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    Preconditions.checkNotNull(author, CANNOT_BE_NULL, "Author");
    Preconditions.checkArgument(!author.isEmpty(), CANNOT_BE_EMPTY, "Author");
    this.author = author;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    Preconditions.checkNotNull(image, CANNOT_BE_NULL, "Image");
    Preconditions.checkArgument(!image.isEmpty(), CANNOT_BE_EMPTY, "Image");
    this.image = image;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    Preconditions.checkNotNull(title, CANNOT_BE_NULL, "Title");
    Preconditions.checkArgument(!title.isEmpty(), CANNOT_BE_EMPTY, "Title");
    this.title = title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    Preconditions.checkNotNull(subtitle, CANNOT_BE_NULL, "Subtitle");
    Preconditions.checkArgument(!subtitle.isEmpty(), CANNOT_BE_EMPTY, "Subtitle");
    this.subtitle = subtitle;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    Preconditions.checkNotNull(publisher, CANNOT_BE_NULL, "Publisher");
    Preconditions.checkArgument(!publisher.isEmpty(), CANNOT_BE_EMPTY, "Publisher");
    this.publisher = publisher;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    Preconditions.checkNotNull(year, CANNOT_BE_NULL, "Year");
    Preconditions.checkArgument(!year.isEmpty(), CANNOT_BE_EMPTY, "Year");
    this.year = year;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    Preconditions.checkNotNull(page, CANNOT_BE_NULL, "Page");
    Preconditions.checkArgument(page > 0, PAGES_MUST_BE_POSITIVE_NUMBER, Integer.toString(page));
    this.page = page;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    Preconditions.checkNotNull(isbn, CANNOT_BE_NULL, "ISBN");
    Preconditions.checkArgument(!isbn.isEmpty(), CANNOT_BE_EMPTY, "ISBN");
    this.isbn = isbn;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    Preconditions.checkNotNull(users, CANNOT_BE_NULL,"Users");
    this.users = users;
  }
}
