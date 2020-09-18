package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static wolox.training.constants.PreconditionsMessages.CANNOT_BE_EMPTY;
import static wolox.training.constants.PreconditionsMessages.CANNOT_BE_NULL;
import static wolox.training.constants.PreconditionsMessages.PAGES_MUST_BE_POSITIVE_NUMBER;

import org.junit.jupiter.api.Test;

public class BookTest {

  private Book book = new Book();

  @Test
  public void whenSetAuthorNull_thenThrowsException() {
    Exception exception = assertThrows(Exception.class, () -> book.setAuthor(null));
    assertThat(exception.getMessage()).isEqualTo(String.format(CANNOT_BE_NULL, "Author"));
  }

  @Test
  public void whenSetTitleEmpty_thenThrowsException(){
    Exception exception = assertThrows(Exception.class, () -> book.setTitle(""));
    assertThat(exception.getMessage()).isEqualTo(String.format(CANNOT_BE_EMPTY, "Title"));
  }

  @Test
  public void whenSetPageNumberToZero_thenThrowsException(){
    Exception exception = assertThrows(Exception.class, () -> book.setPages(0));
    assertThat(exception.getMessage()).isEqualTo(String.format(PAGES_MUST_BE_POSITIVE_NUMBER,"0"));
  }

}
