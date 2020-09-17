package wolox.training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/books")
public class BookController {

  @Autowired
  private BookRepository bookRepository;

  @GetMapping("/greeting")
  public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name",name);
    return "greeting";
  }

  @GetMapping
  public Iterable<Book> findAll(){
    return bookRepository.findAll();
  }

  @GetMapping("/author/{bookAuthor}")
  public Book findByAuthor(@PathVariable String author){
    return bookRepository.findByAuthor(author);
  }

  @GetMapping("/{id}")
  public Book findOne(@PathVariable Long id){
    return bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
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
