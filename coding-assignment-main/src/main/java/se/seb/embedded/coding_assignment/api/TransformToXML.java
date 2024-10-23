package se.seb.embedded.coding_assignment.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.seb.embedded.coding_assignment.api.ProcessingException;
import se.seb.embedded.coding_assignment.service.TransformService;
import se.seb.embedded.coding_assignment.model.Transaction;
import se.seb.embedded.coding_assignment.model.TransactionRequest;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class TransformToXML {

    private final TransformService transformService;

    public TransformToXML(TransformService transformService) {
        this.transformService = transformService;
    }

    @PostMapping("/transformToXML")
    public ResponseEntity<String> transformToXML(@RequestBody TransactionRequest transactionRequest) {
        List<Transaction> transactions = transactionRequest.getTransactions();
        if (Objects.isNull(transactions) || transactions.isEmpty()) {
            return ResponseEntity.badRequest().body("No transactions found");
        }
        Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionDate));
        log.info("Transactions grouped by Date : {}", groupedByDate.size());

        try {
            List<LocalDate> missedDates = transformService.transformToXML(groupedByDate);
            if (!Objects.isNull(missedDates) && missedDates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CREATED).body("XML Files created");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("XML files not created for " + missedDates);
            }
        } catch (ProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception in processing the transactions");
        }
    }
}
