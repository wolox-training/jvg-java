package wolox.training.exception;

public class BookAlreadyOwnedException extends RuntimeException{

  public static final String BOOK_ALREADY_OWNED = "Book already owned. Book Title: %s.";

  public BookAlreadyOwnedException(String bookTitle){
    super(String.format(BOOK_ALREADY_OWNED, bookTitle));
  }
}
