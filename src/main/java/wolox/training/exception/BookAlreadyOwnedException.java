package wolox.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason= "Book Already Owned.")
public class BookAlreadyOwnedException extends RuntimeException{

  public static final String BOOK_ALREADY_OWNED = "Book already owned. Book Title: %s.";

  public BookAlreadyOwnedException(String bookTitle){
    super(String.format(BOOK_ALREADY_OWNED, bookTitle));
  }
}
