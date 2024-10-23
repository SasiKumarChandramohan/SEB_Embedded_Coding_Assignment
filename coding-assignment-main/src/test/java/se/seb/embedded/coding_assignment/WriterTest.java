package se.seb.embedded.coding_assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.seb.embedded.coding_assignment.model.Transaction;
import se.seb.embedded.coding_assignment.model.TransactionCategory;
import se.seb.embedded.coding_assignment.service.Writer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WriterTest {

    private BlockingQueue mockedQueue;
    private Writer writer;

    @BeforeEach
    void setUp() {
        mockedQueue = Mockito.mock(LinkedBlockingDeque.class);
        writer = new Writer(mockedQueue);
    }

    @Test
    void testCallMethodWhenQueueIsEmpty() throws Exception {
        when(mockedQueue.isEmpty()).thenReturn(true);
        writer.call();
        verify(mockedQueue, times(0)).take();
    }

    @Test
    void testCall_interruptedException() throws Exception {
        when(mockedQueue.take()).thenThrow(new InterruptedException("Mock InterruptedException"));
        assertThrows(InterruptedException.class, () -> {
            writer.call();
        });
        verify(mockedQueue, times(1)).take();
    }

    @Test
    void testCallableWithExecutorService() throws Exception {
        Map.Entry<LocalDate, List<Transaction>> entry = new AbstractMap.SimpleEntry<>(LocalDate.now(),
                Arrays.asList(new Transaction(BigDecimal.TEN, "123", TransactionCategory.CREDIT, LocalDate.now(), "SEK")));
        when(mockedQueue.take()).thenReturn(entry);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(writer);
        verify(mockedQueue, times(1)).take();
        executorService.shutdown();
    }


}
