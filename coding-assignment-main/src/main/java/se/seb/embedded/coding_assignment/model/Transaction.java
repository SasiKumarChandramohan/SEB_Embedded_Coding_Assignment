package se.seb.embedded.coding_assignment.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Transaction {

    private BigDecimal amount;
    private String accountNo;
    private TransactionCategory transactionCategory;
    private LocalDate transactionDate;
    private String currency;

    public Transaction() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"amount\":" + amount +
                ", \"accountNo\":\"" + accountNo + '\"' +
                ", \"transactionCategory\":\"" + transactionCategory + "\"" +
                ", \"transactionDate\":\"" + transactionDate + "\"" +
                ", \"currency\":\"" + currency + "\"" +
                '}';
    }
}
