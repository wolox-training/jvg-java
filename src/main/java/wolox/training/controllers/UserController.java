package wolox.training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

  public static final String USER_ID_DOES_NOT_MATCH_WITH_ID_PROVIDED = "User id (%d) does not match with id provided (%d).";
  public static final String USER_WAS_NOT_FOUND_ID = "User was not found. ID: %d";

  @Autowired
  UserRepository userRepository;

  @Autowired
  BookController bookController;

  @GetMapping
  public Iterable<User> getAll(){
    return userRepository.findAll();
  }

  @GetMapping("/{userId}")
  public User findById(@PathVariable Long userId){
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(USER_WAS_NOT_FOUND_ID,userId)));
  }

  @GetMapping("/username/{username}")
  public User findByUsername(@PathVariable String username){
    return userRepository.findByUsername(username);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody User user) {
    return userRepository.save(user);
  }

  @DeleteMapping("/{userId}")
  public void delete(@PathVariable Long userId){
    findById(userId);
    userRepository.deleteById(userId);
  }

  @PutMapping("/{userId}")
  public User update(@RequestBody User user, @PathVariable Long userId){
    if (user.getUserId() != userId){
      throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
          String.format(USER_ID_DOES_NOT_MATCH_WITH_ID_PROVIDED,user.getUserId(), userId));
    }
    findById(userId);
    return userRepository.save(user);
  }

  @PutMapping("/book/{userId}")
  public User addBook(@RequestBody Book book, @PathVariable Long userId){
    User user = findById(userId);
    bookController.findOne(userId);
    user.addBook(book);
    return userRepository.save(user);
  }

  @DeleteMapping("/book/{userId}")
  public User deleteBook(@RequestBody Book book, @PathVariable Long userId){
    User user = findById(userId);
    bookController.findOne(userId);
    user.deleteBook(book);
    return userRepository.save(user);
  }


}
