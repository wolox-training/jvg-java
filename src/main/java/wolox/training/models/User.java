package wolox.training.models;

import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_NULL;
import static wolox.training.constants.PreconditionsConstants.UNBORN;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import wolox.training.exception.BookAlreadyOwnedException;

@Entity
@Table(name="users")
@ApiModel(description = "Users to be related with books.")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="id")
  private long userId;
  @Column(nullable = false)
  private String username;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private LocalDate birthdate;
  @Column(nullable = false)
  @ManyToMany()
  @JoinTable(name = "book",
      joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
  @ApiModelProperty(notes= "A book can be in more than one user book collection.")
  private List<Book> books = new ArrayList<>();

  public User() {
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    Preconditions.checkNotNull(userId,CANNOT_BE_NULL,"UserID");
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    Preconditions.checkNotNull(username, CANNOT_BE_NULL, "Username");
    Preconditions.checkArgument(!username.isEmpty(), CANNOT_BE_EMPTY, "Username");
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Preconditions.checkNotNull(name, CANNOT_BE_NULL, "Name");
    Preconditions.checkArgument(!name.isEmpty(), CANNOT_BE_EMPTY, "Name");
    this.name = name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(LocalDate birthdate) {
    Preconditions.checkNotNull(birthdate, CANNOT_BE_NULL, "Birthdate");
    Preconditions.checkArgument(birthdate.isBefore(LocalDate.now()), UNBORN, birthdate.toString());
    this.birthdate = birthdate;
  }

  public List<Book> getBooks() {
    return (List<Book>) Collections.unmodifiableList(books);
  }

  public void setBooks(List<Book> books) {
    Preconditions.checkNotNull(books, CANNOT_BE_NULL, "Books");
    this.books = books;
  }

  public void addBook(Book book){
    if(this.books.contains(book)){
      throw new BookAlreadyOwnedException(book.getTitle());
    }
    this.books.add(book);
  }

  public void deleteBook(Book book){
    this.books.remove(book);
  }
}
