package com.monzo.webcrawler.core;

import com.monzo.webcrawler.utils.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class EngineObserver {

    private static final Logger log = LoggerFactory.getLogger(EngineObserver.class);
    private final AtomicInteger totalEnqueuedLinks;
    private final AtomicInteger totalProcessedLinks;
    private final CountDownLatch countDownLatch;
    private final Console console;


    EngineObserver(AtomicInteger totalEnqueuedLinks, AtomicInteger totalProcessedLinks, CountDownLatch countDownLatch, Console console) {
        this.totalEnqueuedLinks = totalEnqueuedLinks;
        this.totalProcessedLinks = totalProcessedLinks;
        this.countDownLatch = countDownLatch;
        this.console = console;
    }

    public static EngineObserver instance() {
        return new EngineObserver(new AtomicInteger(), new AtomicInteger(), new CountDownLatch(1), Console.instance());
    }

    public void incrementEnqueuedLinks() {
        totalEnqueuedLinks.incrementAndGet();
    }

    public void incrementProcessedLinks() {
        totalProcessedLinks.incrementAndGet();
    }

    public boolean isTerminated() {
        return totalEnqueuedLinks.intValue() == totalProcessedLinks.intValue();
    }

    public void awaitTermination() throws InterruptedException {
        log.debug("waiting...");
        countDownLatch.await();
        log.debug("process completed...");
    }

    public void notifyTermination() {
        countDownLatch.countDown();
    }

    public void printProcessSummary() {
        console.println("Enqueued Links: %d -- Processed Links: %d", totalEnqueuedLinks.intValue(), totalProcessedLinks.intValue());
        console.println();
    }
}
