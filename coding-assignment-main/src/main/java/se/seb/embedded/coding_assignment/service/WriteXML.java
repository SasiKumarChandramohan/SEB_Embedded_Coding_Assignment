package se.seb.embedded.coding_assignment.service;

import lombok.extern.slf4j.Slf4j;
import se.seb.embedded.coding_assignment.api.ProcessingException;
import se.seb.embedded.coding_assignment.model.Transaction;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class WriteXML {
    private static final String OUTPUT_DIR = "output/";
    private static final String CREDIT = "CREDIT";
    private static final String DEBIT = "DEBIT";
    /**
     * Method to write XML without storing in memory
     * @param date
     * @param transactions
     * @return
     * @throws IOException
     */
    public LocalDate generateXML(LocalDate date, List<Transaction> transactions) throws IOException {
        if(transactions == null || transactions.isEmpty()) throw new IllegalArgumentException("Transactions cannot be empty");

        Path filePath = Paths.get(OUTPUT_DIR);
        Files.createDirectories(filePath);
        try (BufferedWriter writer = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(OUTPUT_DIR + date + ".xml"), StandardCharsets.UTF_8))) {

            writer.write(genXmlOpen());
            writeHeader(date, transactions, writer);

            writer.write("\t\t<transactionSummary>\n");
            writeTransactionSummary(transactions, CREDIT, writer);
            writeTransactionSummary(transactions, DEBIT, writer);
            writer.write("\t\t</transactionSummary>\n");

            writer.write("\t</header>\n");

            writeTransactionsByAccount(transactions, writer);

            writer.write("</document>");
            log.info("XML file written for date {}", date);
            return date;
        } catch (IOException e) {
            throw new ProcessingException("Exception in writing XML file for " + date);
        }

    }

    /**
     * Method to write header section of the XML
     * @param date
     * @param transactions
     * @param writer
     * @throws IOException
     */
    private void writeHeader(LocalDate date, List<Transaction> transactions, BufferedWriter writer) throws IOException {
        writer.write("\n<document>\n \t<header>\n \t\t<date>");
        writer.write(date.toString() + "</date>\n");
        writer.write("\t\t<numberOfTransactions>");
        writer.write(String.valueOf(transactions.size()));
        writer.write("</numberOfTransactions>\n");
        writer.write("\t\t<fileId>");
        writer.write(UUID.randomUUID().toString());
        writer.write("</fileId>\n");
    }

    /**
     * Method to write transaction summary section of the XML
     * @param transactions
     * @param transactionCategory
     * @param writer
     * @throws IOException
     */
    private void writeTransactionSummary(List<Transaction> transactions, String transactionCategory, BufferedWriter writer) throws IOException {
        String currency = transactions.get(0).getCurrency();
        if(transactionCategory.equals(CREDIT)){
            writer.write("\t\t\t<creditSummary>\n");
        }else {
            writer.write("\t\t\t<debitSummary>\n");
        }
        writer.write("\t\t\t\t<numberOfTransactions>");
        long count = getNumberOfTransaction(transactions, transactionCategory);
        writer.write(String.valueOf(count));
        writer.write("</numberOfTransactions>\n");
        if(count!=0){
            writer.write("\t\t\t\t<totalAmount currency=\"");
            writer.write(currency + "\">");
            writer.write(getTotalAmount(transactions,transactionCategory));
            writer.write("</totalAmount>\n");
        }
        if(transactionCategory.equals(CREDIT)){
            writer.write("\t\t\t</creditSummary>\n");
        }else {
            writer.write("\t\t\t</debitSummary>\n");
        }

    }

    /**
     * Method to write Transaction Details per account
     * @param transactions
     * @param writer
     * @throws IOException
     */
    private void writeTransactionsByAccount(List<Transaction> transactions ,BufferedWriter writer) throws IOException {

        Map<String, List<Transaction>> transactionsByAccount = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAccountNo));
        for (Map.Entry<String, List<Transaction>> entry : transactionsByAccount.entrySet()) {
            writer.write("\t<transactions>\n \t\t<accountNumber>");
            writer.write(entry.getKey());
            writer.write("</accountNumber>\n");

            Iterator<Transaction> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                Transaction transaction = iterator.next();
                writer.write(transaction.getTransactionCategory().getValue().equals(CREDIT) ? "\t\t<credit" : "\t\t<debit");
                writer.write(" currency=" +  "\"" + transaction.getCurrency() + "\">");
                writer.write(transaction.getAmount().toString());
                writer.write(transaction.getTransactionCategory().getValue().equals(CREDIT) ? "</credit>\n" : "</debit>\n");
                writer.write("\t</transactions>\n");
                if(iterator.hasNext()) {
                    writer.write("\t<transactions>\n");
                }
            }
        }
    }

    /**
     * Method to get the total amount of transactions for a CREDIT/DEBIT
     * @param transactions
     * @param transactionCategory
     * @return
     */
    private String getTotalAmount(List<Transaction> transactions , String transactionCategory){
        BigDecimal totalAmount =  transactions
                .stream()
                .filter(transaction -> transaction.getTransactionCategory().getValue().equals(transactionCategory))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.toString();
    }

    /**
     * Opening XML Tag
     * @return
     */
    private String genXmlOpen() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    }

    /**
     * Method to get the number of transactions based on CREDIT/DEBIT
     * @param transactions
     * @param transactionCategory
     * @return
     */
    private long getNumberOfTransaction(List<Transaction> transactions , String transactionCategory){
        return transactions
                .stream()
                .filter(transaction -> transaction.getTransactionCategory().getValue().equals(transactionCategory))
                .count();
    }

}
