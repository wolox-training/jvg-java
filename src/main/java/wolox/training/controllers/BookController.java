package wolox.training.controllers;

import static wolox.training.constants.ExceptionConstants.BOOK_WAS_NOT_FOUND_ISBN;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exception.BookIdMismatchException;
import wolox.training.exception.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.service.OpenLibraryService;

@RestController
@RequestMapping("/api/books")
public class BookController {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private OpenLibraryService openLibraryService;

  @GetMapping("/greeting")
  public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name",name);
    return "greeting";
  }

  @GetMapping
  public Iterable<Book> findAll(
      @RequestParam(required=false, defaultValue = "") String genre,
      @RequestParam(required=false, defaultValue = "") String author,
      @RequestParam(required=false, defaultValue = "") String image,
      @RequestParam(required=false, defaultValue = "") String title,
      @RequestParam(required=false, defaultValue = "") String subtitle,
      @RequestParam(required=false, defaultValue = "") String publisher,
      @RequestParam(required=false, defaultValue = "") String year,
      @RequestParam(required=false, defaultValue = "") String pages,
      @RequestParam(required=false, defaultValue = "") String isbn){
    Integer pagesInt = pages.isEmpty() ? null : Integer.parseInt(pages);
    return bookRepository.findAllByFilters(genre, author, image, title, subtitle, publisher, year, pagesInt, isbn);
  }

  @GetMapping("/author/{author}")
  public Book findByAuthor(@PathVariable String author){
    return bookRepository.findByAuthor(author)
        .orElseThrow(() -> new BookNotFoundException(author));
  }

  @GetMapping("/{id}")
  public Book findOne(@PathVariable Long id){
    return bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  @GetMapping("/find")
  public Iterable<Book> findByPublisherGenreAndYear(
      @RequestParam(required = false) String publisher,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String year){
    return bookRepository.findAllByPublisherAndGenreAndYear(publisher, genre, year);
  }

  @GetMapping("/isbn/{isbn}")
  public ResponseEntity<Book> findByISBN(@PathVariable String isbn){
    Optional<Book> optBook = bookRepository.findByIsbn(isbn);
    if(optBook.isPresent()){
      return new ResponseEntity<>(optBook.get(),HttpStatus.OK);
    }
    try {
      Book book = openLibraryService.bookInfo(isbn).orElseThrow(() ->
          new BookNotFoundException(String.format(BOOK_WAS_NOT_FOUND_ISBN,isbn)));
      return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);
    } catch (JsonProcessingException e) {
      throw new BookNotFoundException(String.format(BOOK_WAS_NOT_FOUND_ISBN,isbn));
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book create(@RequestBody Book book) {
    return bookRepository.save(book);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id){
    bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    bookRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  public Book updateBook(@RequestBody Book book, @PathVariable Long id ){
    if (book.getId() != id) {
      throw new BookIdMismatchException(book.getId(), id);
    }
    bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    return bookRepository.save(book);
  }

}
