package com.monzo.webcrawler.core;

import com.monzo.webcrawler.utils.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EngineObserverTest {

    AtomicInteger totalEnqueuedLinks;
    AtomicInteger totalProcessedLinks;
    @Mock
    CountDownLatch countDownLatch;
    @Mock
    Console console;

    EngineObserver engineObserver;

    @BeforeEach
    public void init() {
        totalEnqueuedLinks = new AtomicInteger();
        totalProcessedLinks = new AtomicInteger();
//        countDownLatch = new CountDownLatch(1);
        engineObserver = new EngineObserver(totalEnqueuedLinks, totalProcessedLinks, countDownLatch, console);
    }

    @Test
    void whenInstanceMethodIsInvoked_thenANewObserverInstanceIsReturned() {
        EngineObserver result = EngineObserver.instance();

        assertNotNull(result);
        assertInstanceOf(EngineObserver.class, result);
    }

    @Test
    void whenIncrementProcessedLinks_thenTotalProcessedLinksIsIncremented() {
        engineObserver.incrementProcessedLinks();

        assertEquals(1, totalProcessedLinks.intValue());
    }

    @Test
    void whenIncrementEnqueuedLinks_thenTotalEnqueuedLinksIsIncremented() {
        engineObserver.incrementEnqueuedLinks();

        assertEquals(1, totalEnqueuedLinks.intValue());
    }

    @Test
    void givenEnqueuedLinksEqualsToProcessedLinks_whenIsTerminated_thenItReturnsTrue() {
        totalEnqueuedLinks.addAndGet(3);
        totalProcessedLinks.addAndGet(3);

        assertTrue(engineObserver.isTerminated());
    }

    @Test
    void givenEnqueuedLinksDiffersFromProcessedLinks_whenIsTerminated_thenItReturnsTrue() {
        totalEnqueuedLinks.addAndGet(5);
        totalProcessedLinks.addAndGet(3);

        assertFalse(engineObserver.isTerminated());
    }

    @Test
    void whenAwaitTerminationIsCalled_thenCountDownLatchAwaitIsCalled() throws InterruptedException {
        engineObserver.awaitTermination();

        verify(countDownLatch).await();
    }

    @Test
    void whenNotifyTerminationIsInvoked_thenCountDownLatchCountDownIsCalled() {
        engineObserver.notifyTermination();

        verify(countDownLatch).countDown();
    }
}