package wolox.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.OK, reason= "Book Already Owned.")
public class BookAlreadyOwnedException extends RuntimeException{

  public static final String BOOK_ID_DOESNT_MATCH = "Book already owned. Book Title: %s.";

  public BookAlreadyOwnedException(String bookTitle){
    super(String.format(BOOK_ID_DOESNT_MATCH, bookTitle));
  }
}
