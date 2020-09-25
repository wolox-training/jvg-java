package wolox.training.controllers;

import static wolox.training.constants.ExceptionConstants.USER_ID_DOES_NOT_MATCH_WITH_ID_PROVIDED;
import static wolox.training.constants.ExceptionConstants.USER_WAS_NOT_FOUND_ID;
import static wolox.training.constants.ExceptionConstants.USER_WAS_NOT_FOUND_USERNAME;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.server.ResponseStatusException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
@Api
public class UserController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  BookController bookController;

  @Autowired
  PasswordEncoder passwordEncoder;

  @GetMapping("/logged")
  public User getActualUser(Authentication authentication){
    return userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(USER_WAS_NOT_FOUND_USERNAME, authentication.getName())));
  }

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
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(USER_WAS_NOT_FOUND_USERNAME,username)));
  }

  @GetMapping("/find")
  public Iterable<User> findByBirthdateAndName(
      @RequestParam String startDateStr,
      @RequestParam String endDateStr,
      @RequestParam String name){
    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);
    return userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(startDate, endDate, name);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
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
    User userDB = findById(userId);
    user.setPassword(userDB.getPassword());
    return userRepository.save(user);
  }

  @PutMapping("/{userId}/books/add")
  @ApiOperation(value="Giving a user id and a book, the book is added to the user's collection.", response = User.class)
  @ApiResponses(value = {
      @ApiResponse(code=200, message = "Successfully added the book."),
      @ApiResponse(code=404, message = "The user/book doesn't exists in DB.")
  })
  public User addBook(@ApiParam(value="book to be added.",required = true) @RequestBody Book book,
      @ApiParam(value= "user id to add the book.", required = true) @PathVariable Long userId){
    User user = findById(userId);
    bookController.findOne(book.getId());
    user.addBook(book);
    return userRepository.save(user);
  }

  @DeleteMapping("/{userId}/books/delete")
  public User deleteBook(@RequestBody Book book, @PathVariable Long userId){
    User user = findById(userId);
    bookController.findOne(book.getId());
    user.deleteBook(book);
    return userRepository.save(user);
  }


}
