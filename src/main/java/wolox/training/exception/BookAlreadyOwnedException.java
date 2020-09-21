package wolox.training.exception;

import static wolox.training.constants.ExceptionConstants.BOOK_ALREADY_OWNED;

public class BookAlreadyOwnedException extends RuntimeException{

  public BookAlreadyOwnedException(String bookTitle){
    super(String.format(BOOK_ALREADY_OWNED, bookTitle));
  }
}
