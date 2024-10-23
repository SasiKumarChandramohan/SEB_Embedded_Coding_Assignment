package se.seb.embedded.coding_assignment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

import se.seb.embedded.coding_assignment.model.Transaction;
import se.seb.embedded.coding_assignment.model.TransactionCategory;
import se.seb.embedded.coding_assignment.model.TransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CodingAssignmentApplication.class)
@Import({
        TestClient.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransformToXMLTest{
    @Autowired
    private TestClient testClient;

    @Test
    public void transformToXMLReturnProperResponse() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionCategory(TransactionCategory.CREDIT);
        transaction.setAmount(BigDecimal.valueOf(10));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setAccountNo("12345");
        transaction.setCurrency("SEK");
        transactions.add(transaction);
        TransactionRequest request = new TransactionRequest();
        request.setTransactions(transactions);
        ResponseEntity<String> response =  sendTransformToXML(request);
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo("XML Files created");
    }
    @Test
    public void inputRequestNull() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactions(null);
        ResponseEntity<String> response =  sendTransformToXML(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("No transactions found");
    }

    @Test
    public void inputRequestEmpty() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactions(new ArrayList<>());
        ResponseEntity<String> response =  sendTransformToXML(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("No transactions found");
    }

    private ResponseEntity<String> sendTransformToXML(TransactionRequest request) {
        return testClient.transformToXML(request);
    }



}

