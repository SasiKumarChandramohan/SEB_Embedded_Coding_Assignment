package se.seb.embedded.coding_assignment.model;

import lombok.*;

import java.util.List;

@Setter
@Getter
public class TransactionRequest {

    private List<Transaction> transactions;

}
