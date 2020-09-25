package wolox.training.models;

import static wolox.training.constants.PreconditionsConstants.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsConstants.UNBORN;

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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import wolox.training.exception.BookAlreadyOwnedException;

@Entity
@Table(name="users")
@ApiModel(description = "Users to be related with books.")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "user")
@NoArgsConstructor
@Getter
@NonNull
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
  @Column(name="user_type", insertable = false, updatable = false)
  @JsonProperty("user_type")
  private String userType;

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public void setUsername(String username) {
    Preconditions.checkArgument(!username.isEmpty(), CANNOT_BE_EMPTY, "Username");
    this.username = username;
  }

  public void setName(String name) {
    Preconditions.checkArgument(!name.isEmpty(), CANNOT_BE_EMPTY, "Name");
    this.name = name;
  }

  public void setBirthdate(LocalDate birthdate) {
    Preconditions.checkArgument(birthdate.isBefore(LocalDate.now()), UNBORN, birthdate.toString());
    this.birthdate = birthdate;
  }

  public List<Book> getBooks() {
    return (List<Book>) Collections.unmodifiableList(books);
  }

  public void setBooks(List<Book> books) {
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

  public void setPassword(String password) {
    this.password = password;
  }
}
