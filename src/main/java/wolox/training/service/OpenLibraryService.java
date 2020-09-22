package wolox.training.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import wolox.training.models.Book;

@Service
public class OpenLibraryService {

  @Value("${openlibrary.url}")
  private String base_uri;

  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper objectMapper = new ObjectMapper();

  public Optional<Book> bookInfo(String isbn) throws JsonProcessingException {
    JsonNode jsonBook = getBookFromExternalAPI(isbn);
    if(jsonBook.isEmpty()){
      return Optional.empty();
    }
    Book book = new Book();
    book.setIsbn(isbn);
    book.setTitle(jsonBook.get("title").asText());
    book.setSubtitle(jsonBook.get("subtitle").asText());
    book.setImage(jsonBook.get("cover").get("medium").asText());
    book.setPublisher(jsonBook.get("publishers").get(0).get("name").asText()); //LF method parse the array to the string
    book.setYear(jsonBook.get("publish_date").asText());
    book.setPages(jsonBook.get("number_of_pages").asInt());
    book.setAuthor(jsonBook.get("authors").get(0).get("name").asText());

    return Optional.of(book);
  }

  private JsonNode getBookFromExternalAPI(String isbn) throws JsonProcessingException {
    String resource = "/api/books";
    String uri = UriComponentsBuilder.fromHttpUrl(base_uri+resource)
        .queryParam("bibkeys", "ISBN:" + isbn)
        .queryParam("format","json")
        .queryParam("jscmd","data")
        .toUriString();
    ResponseEntity<String> apiResponse = restTemplate.getForEntity(uri, String.class);
    return apiResponse.hasBody() ?
        objectMapper.readTree(apiResponse.getBody()).get("ISBN:".concat(isbn)) :
        objectMapper.createObjectNode();
  }
}
