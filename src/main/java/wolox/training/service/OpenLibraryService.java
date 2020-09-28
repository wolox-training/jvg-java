package wolox.training.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
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
    book.setImage(jsonBook.get("cover").get("small").asText());
    book.setPublisher(getJsonNodeArrayNamesToString(jsonBook.get("publishers"))); //LF method parse the array to the string
    book.setYear(jsonBook.get("publish_date").asText());
    book.setPages(jsonBook.get("number_of_pages").asInt());
    book.setAuthor(getJsonNodeArrayNamesToString(jsonBook.get("authors")));

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
    return !Objects.equals(apiResponse.getBody(), "{}") ?
        objectMapper.readTree(apiResponse.getBody()).get("ISBN:".concat(isbn)) :
        objectMapper.createObjectNode();
  }

  private String getJsonNodeArrayNamesToString(JsonNode jsonArray){
    String result = "";
    if(jsonArray.isEmpty()){
      return result;
    }
    StringBuilder resultBuilder = new StringBuilder();
    for (JsonNode node : jsonArray) {
      resultBuilder.append(node.get("name").asText());
      resultBuilder.append(",");
    }
    result = resultBuilder.toString();
    return result.isEmpty() ? result : result.substring(0, resultBuilder.toString().length() -1);
  }
}
