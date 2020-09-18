package wolox.training.controllers;

import wolox.training.models.Book;

public class BookFactory {

  public BookFactory(){}

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
    book.setId(1);
    return book;
  }

}
