package wolox.training.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.FileReader;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import wolox.training.models.Book;

@SpringBootTest(properties = {"openlibrary.url=https://openlibrary.org"})
public class OpenLibraryServiceTest {

  @Autowired
  OpenLibraryService openLibraryService;

  private static WireMockServer wireMockServer;
  private static final String validISBN = "0385472579";
  private static final String invalidISBN = "11223344";

  @BeforeAll
  public static void setWireMockServer(){
    String url = "/api/books?bibkeys=ISBN:%s&format=json&jscmd=data";
    wireMockServer = new WireMockServer(options().bindAddress("localhost")
        .port(8082).usingFilesUnderDirectory("src/test/resources"));

    wireMockServer.stubFor(WireMock.get(urlEqualTo(String.format(url,validISBN)))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("response_ok_book")));

    wireMockServer.stubFor(WireMock.get(urlEqualTo(String.format(url,invalidISBN)))
    .willReturn(aResponse()
    .withStatus(200)
    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .withBodyFile("response_empty_body")));

    wireMockServer.start();
  }

  @Test
  public void whenFindByISBN_thenReturnBook() throws Exception {

    Optional<Book> response = openLibraryService.bookInfo(validISBN);

    assertThat(response).isPresent();
    assertThat(response.get().getTitle()).isEqualTo("Zen speaks");
  }

  @Test
  public void whenFindByISBN_thenReturnEmpty() throws Exception {

    Optional<Book> response = openLibraryService.bookInfo(invalidISBN);

    assertThat(response).isNotPresent();
  }

}
