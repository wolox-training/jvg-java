package wolox.training.exception;


public class BookNotFoundException extends RuntimeException {

  public static final String BOOK_WAS_NOT_FOUND = "Book was not found. ID: %d";
  public static final String BOOK_WAS_NOT_FOUND_AUTHOR = "Book was not found. Author: %s";

  public BookNotFoundException(Long id) {
    super(String.format(BOOK_WAS_NOT_FOUND,id));
  }

  public BookNotFoundException(String author){
    super(String.format(BOOK_WAS_NOT_FOUND_AUTHOR,author));
  }
}
