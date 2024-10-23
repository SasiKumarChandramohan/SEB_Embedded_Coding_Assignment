package se.seb.embedded.coding_assignment;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import se.seb.embedded.coding_assignment.model.TransactionRequest;

import java.util.List;

@Lazy
@TestComponent
public class TestClient {

    private final TestRestTemplate restTemplate;
    private final String host;

    public TestClient(TestRestTemplate restTemplate, @LocalServerPort int port) {
        this.restTemplate = restTemplate;
        this.host = "http://localhost:" + port;
    }

    public ResponseEntity<String> transformToXML(TransactionRequest transactionRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return restTemplate.exchange(host + "/transformToXML", HttpMethod.POST, new HttpEntity<>(transactionRequest, headers), String.class);
    }

}
