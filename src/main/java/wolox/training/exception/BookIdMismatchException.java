package wolox.training.exception;


import static wolox.training.constants.ExceptionConstants.BOOK_ID_DOESNT_MATCH;

public class BookIdMismatchException extends RuntimeException {

  public BookIdMismatchException(Long bookId, Long id){
    super(String.format(BOOK_ID_DOESNT_MATCH, bookId, id));
  }
}
