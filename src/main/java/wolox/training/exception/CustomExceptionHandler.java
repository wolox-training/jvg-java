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

  @ExceptionHandler(value = {BookNotFoundException.class})
  protected ResponseEntity<Object> handleBookNotFoundException(RuntimeException exception, WebRequest request){
    return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
        HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(value = {BookIdMismatchException.class, BookAlreadyOwnedException.class})
  protected ResponseEntity<Object> handleUnprocessableEntityBookException(RuntimeException exception, WebRequest request){
    return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
        HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

}
