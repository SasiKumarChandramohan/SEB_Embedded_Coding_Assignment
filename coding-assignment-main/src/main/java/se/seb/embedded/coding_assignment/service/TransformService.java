package se.seb.embedded.coding_assignment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.seb.embedded.coding_assignment.api.ProcessingException;
import se.seb.embedded.coding_assignment.model.Transaction;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TransformService {

    private static ExecutorService workerPool = null;

    /**
     * Method to process the transactions through Processor and Writer
     * @param groupedByDate
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<LocalDate> transformToXML(Map<LocalDate, List<Transaction>> groupedByDate) throws ExecutionException, InterruptedException {
        BlockingQueue queue = new LinkedBlockingDeque<>();
        try{
            Processor processor = new Processor(queue, groupedByDate);
            Writer writer = new Writer(queue);
            workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Future<List<LocalDate>> processorFuture = workerPool.submit(processor);
            log.info("Processing Task submitted");
            List<LocalDate> processedDates =  processorFuture.get();
            Future<List<LocalDate>> writerFeature = workerPool.submit(writer);
            log.info("Writing Task submitted");
            List<LocalDate> writtenDates = writerFeature.get();
            return findMissingDates(processedDates, writtenDates);
        } catch (Exception e) {
            throw new ProcessingException("Error in Processing the transactions " + e.getMessage());
        } finally {
            workerPool.shutdown();
            try{
                if(!workerPool.awaitTermination(1, TimeUnit.MINUTES)){
                    workerPool.shutdownNow();
                }
            }catch(InterruptedException e){
                workerPool.shutdownNow();
            }
        }
    }

    /**
     * Method to return the dates for which xml writing has failed
     * @param processedDates
     * @param writtenDates
     * @return
     */
    private List<LocalDate> findMissingDates(List<LocalDate> processedDates, List<LocalDate> writtenDates) {
         if (Objects.isNull(writtenDates) || writtenDates.isEmpty()) return processedDates;

         List<LocalDate> missingDates = new ArrayList<>(processedDates);
         missingDates.removeAll(writtenDates);
         return missingDates;
    }

}
