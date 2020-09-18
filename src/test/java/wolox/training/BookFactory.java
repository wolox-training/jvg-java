package wolox.training;

import wolox.training.models.Book;

public class BookFactory {
  public BookFactory() {}

  public Book createTestBook(){
    Book book = new Book();
    book.setAuthor("Author");
    book.setImage("Image");
    book.setTitle("Title");
    book.setSubtitle("Subtitle");
    book.setPublisher("Publisher");
    book.setYear("Year");
    book.setPages(20);
    book.setIsbn("ISBN");
    book.setGenre("Genre");
    return book;
  }

  public String getJsonBook(Book book){
    return "{\"genre\": \"" + book.getGenre() + "\"," +
        "\"author\": \"" + book.getAuthor() + "\"," +
        "\"image\": \"" + book.getImage() + "\"," +
        "\"title\": \"" + book.getTitle() + "\"," +
        "\"subtitle\": \"" + book.getSubtitle() + "\"," +
        "\"publisher\": \"" + book.getPublisher() + "\"," +
        "\"year\": \"" + book.getYear() + "\"," +
        "\"pages\": \"" + book.getPages() + "\"," +
        "\"isbn\": \"" + book.getIsbn() + "\"," +
        "\"id\": \"" + book.getId() + "\"" +
        "}";
  }
}
