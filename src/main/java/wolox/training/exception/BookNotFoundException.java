package wolox.training.exception;


import static wolox.training.constants.ExceptionConstants.BOOK_WAS_NOT_FOUND;
import static wolox.training.constants.ExceptionConstants.BOOK_WAS_NOT_FOUND_AUTHOR;

public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException(Long id) {
    super(String.format(BOOK_WAS_NOT_FOUND,id));
  }

  public BookNotFoundException(String author){
    super(String.format(BOOK_WAS_NOT_FOUND_AUTHOR,author));
  }
}
