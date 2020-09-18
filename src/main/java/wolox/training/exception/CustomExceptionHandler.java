package wolox.training.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {BookIdMismatchException.class, BookNotFoundException.class})
  protected ResponseEntity<Object> handleBooksExceptions(RuntimeException exception, WebRequest request){
    HttpStatus httpStatus;
    if(exception.getClass().equals(BookNotFoundException.class)){
      httpStatus = HttpStatus.NOT_FOUND;
    } else {
      httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    }
    return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
        httpStatus, request);
  }
}
