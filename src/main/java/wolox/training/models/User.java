package wolox.training.models;

import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_NULL;
import static wolox.training.constants.PreconditionsConstants.UNBORN;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
  @Column(nullable = false, unique = true)
  private String username;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private LocalDate birthdate;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String password;
  @ManyToMany()
  @ApiModelProperty(notes= "A book can be in more than one user book collection.")
  @JsonIgnoreProperties(value = "users")
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
    if(this.books.stream().anyMatch(bookInList -> bookInList.getId() == book.getId())){
      throw new BookAlreadyOwnedException(book.getTitle());
    }
    this.books.add(book);
  }

  public void deleteBook(Book book){
    this.setBooks(this.books.stream()
        .filter(bookInList -> bookInList.getId() != book.getId())
        .collect(Collectors.toList()));
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
