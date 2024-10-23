package se.seb.embedded.coding_assignment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.seb.embedded.coding_assignment.model.Transaction;
import se.seb.embedded.coding_assignment.model.TransactionCategory;
import se.seb.embedded.coding_assignment.service.WriteXML;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteXMLTest {
    private WriteXML writeXML;
    private Path tempDir;
    private boolean skipTearDown = false;

    @BeforeEach
    public void setUp() {
        writeXML = new WriteXML();
        tempDir = Paths.get("output/");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if(!skipTearDown)
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(file -> file.delete());
    }

    @Test
    public void testTransformXML_SingleTransaction() throws IOException, InterruptedException {
        List<Transaction> transactions = Arrays.asList( getTransactions().get(0));
        Thread.sleep(100);
        writeXML.generateXML(LocalDate.now(), transactions);
        Path expectedFile = tempDir.resolve(LocalDate.now().toString() + ".xml");
        assertTrue(Files.exists(expectedFile));
    }

    @Test
    public void testTransformXML_TwoTransaction() throws IOException, InterruptedException {
        List<Transaction> transactions = Arrays.asList( getTransactions().get(0) ,
                new Transaction(BigDecimal.TEN, "123", TransactionCategory.DEBIT, LocalDate.now(), "SEK") );
        writeXML.generateXML(LocalDate.now(), transactions);
        Thread.sleep(100);
        Path expectedFile = tempDir.resolve(LocalDate.now().toString() + ".xml");
        assertTrue(Files.exists(expectedFile));
    }

    private List<Transaction> getTransactions() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate currentDayPlus2 = LocalDate.now().plusDays(2);
        return Arrays.asList(
                new Transaction(BigDecimal.TEN, "123", TransactionCategory.CREDIT, LocalDate.now(), "SEK"),
                new Transaction(BigDecimal.TEN, "123", TransactionCategory.CREDIT, tomorrow, "SEK"),
                new Transaction(BigDecimal.TEN, "123", TransactionCategory.CREDIT, currentDayPlus2, "SEK"));
    }

    @Test
    public void testSaveToXML_MultipleTransactions() throws IOException {
        List<Transaction> transactions = getTransactions();
        for (Transaction transaction : transactions) {
            writeXML.generateXML(transaction.getTransactionDate(), transactions);
        }

        Path expectedFile1 = tempDir.resolve(transactions.get(0).getTransactionDate() + ".xml");
        Path expectedFile2 = tempDir.resolve(transactions.get(1).getTransactionDate() + ".xml");
        Path expectedFile3 = tempDir.resolve(transactions.get(2).getTransactionDate() + ".xml");
        assertTrue(Files.exists(expectedFile1));
        assertTrue(Files.exists(expectedFile2));
        assertTrue(Files.exists(expectedFile3));

    }

    @Test
    public void testSaveToXML_InvalidDate() {
        skipTearDown = true;
        assertThrows(IllegalArgumentException.class, () -> {
            writeXML.generateXML(LocalDate.now(), null);
        }, "Transactions cannot be empty");

    }
}

