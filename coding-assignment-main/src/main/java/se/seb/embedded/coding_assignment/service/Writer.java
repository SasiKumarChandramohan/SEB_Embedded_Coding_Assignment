package se.seb.embedded.coding_assignment.service;

import lombok.extern.slf4j.Slf4j;
import se.seb.embedded.coding_assignment.model.Transaction;

import java.io.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
@Slf4j
public class Writer implements Callable<List<LocalDate>> {

    private BlockingQueue queue;
    protected List<LocalDate> writtenDates = new ArrayList<>();
    public Writer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public List<LocalDate> call() throws IOException, InterruptedException {
        return write();
    }

    /**
     * Method to consume each day's transaction from BlockingQueue
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private List<LocalDate> write() throws IOException, InterruptedException {
        while (true) {
            if (!queue.isEmpty()) {
                Map.Entry<LocalDate, List<Transaction>> eachDateEntry =(Map.Entry<LocalDate, List<Transaction>>) queue.take();
                log.info("Writing transaction {} ", eachDateEntry.getValue());
                WriteXML writeXML = new WriteXML();
                LocalDate writtenDate = writeXML.generateXML(eachDateEntry.getKey(), eachDateEntry.getValue());
                writtenDates.add(writtenDate);
            }else{
                break;
            }
        }
        return writtenDates;
    }


}
