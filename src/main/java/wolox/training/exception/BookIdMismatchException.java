package wolox.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason= "Book Id Mismatch.")
public class BookIdMismatchException extends RuntimeException {

  public static final String BOOK_ID_DOESNT_MATCH = "Book Id doesn't match. Book ID: %d, Provided ID: %d";

  public BookIdMismatchException(Long bookId, Long id){
    super(String.format(BOOK_ID_DOESNT_MATCH, bookId, id));
  }
}
