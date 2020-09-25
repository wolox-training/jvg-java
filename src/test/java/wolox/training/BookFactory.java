package wolox.training;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import wolox.training.models.Book;

public class BookFactory {

  ObjectMapper objectMapper = new ObjectMapper();

  public BookFactory() {}

  public Book createTestBook(){
    Book book = new Book();
    book.setAuthor("Author");
    book.setImage("Image");
    book.setTitle("Title");
    book.setSubtitle("Subtitle");
    book.setPublisher("Publisher");
    book.setYear("2020");
    book.setPages(20);
    book.setIsbn("ISBN");
    book.setGenre("Genre");
    return book;
  }

  public String getJsonBook(Book book) throws JsonProcessingException {
    return objectMapper.writeValueAsString(book);
  }
}
