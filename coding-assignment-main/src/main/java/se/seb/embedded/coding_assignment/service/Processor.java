package se.seb.embedded.coding_assignment.service;

import lombok.extern.slf4j.Slf4j;
import se.seb.embedded.coding_assignment.model.Transaction;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Slf4j
public class Processor implements Callable<List<LocalDate>> {
    protected BlockingQueue queue = null;
    protected Map<LocalDate, List<Transaction>> transactionsByDate = null;
    protected List<LocalDate> processedDates = new ArrayList<>();
    public Processor(BlockingQueue queue, Map<LocalDate, List<Transaction>> transactionsByDate) {
            this.queue = queue;
            this.transactionsByDate = transactionsByDate;
    }

    @Override
    public List<LocalDate> call(){
        return process(this.transactionsByDate);
    }

    /**
     * Process each days of transaction and add into BlockingQueue to be consumed by the Writer
     * @param transactionsByDate
     * @return
     */
    private List<LocalDate> process(Map<LocalDate, List<Transaction>> transactionsByDate) {
        try {
            for (Map.Entry<LocalDate, List<Transaction>> entry : transactionsByDate.entrySet()) {
                queue.put(entry);
                processedDates.add(entry.getKey());
                log.info("Processing transaction {} ", entry.getValue());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while processing transactions", e);
        }
        return processedDates;
    }
}
