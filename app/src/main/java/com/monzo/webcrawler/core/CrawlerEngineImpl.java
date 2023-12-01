package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.utils.Console;
import com.monzo.webcrawler.web.WebParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.monzo.webcrawler.utils.URLFormatter.getDomainName;

public class CrawlerEngineImpl implements CrawlerEngine {
    private static final Logger log = LoggerFactory.getLogger(CrawlerEngineImpl.class);

    private static final int CONCURRENCY_LEVEL = 30;

    private final String domain;
    private final Set<String> linkHistory;
    private final AtomicInteger totalEnqueuedLinks;
    private final AtomicInteger totalProcessedLinks;
    private final ExecutorService executorService;
    private final CountDownLatch countDownLatch;
    private final Console console;
    private final DisplayManager displayManager;

    CrawlerEngineImpl(URI rootUri, ExecutorService executorService, Console console, DisplayManager displayManager) {
        this.domain = getDomainName(rootUri);
        this.linkHistory = new HashSet<>();
        this.totalEnqueuedLinks = new AtomicInteger();
        this.totalProcessedLinks = new AtomicInteger();
        this.countDownLatch = new CountDownLatch(1);
        this.executorService = executorService;
        this.console = console;
        this.displayManager = displayManager;

        enqueue(rootUri);
    }

    public static CrawlerEngine create(URI rootUrl) {
        log.debug("Creating CrawlerService with rootUrl {}", rootUrl);

        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();

        return new CrawlerEngineImpl(
                rootUrl,
                Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory),
                Console.instance(),
                DisplayManager.instance()
        );
    }

    @Override
    synchronized public void processResult(ParseResult parseResult) {
        try {
            displayManager.printResult(parseResult);

            if (parseResult.isSuccess()) {
                enqueueValidLinks(parseResult.links());
            }
        }
        finally {
            totalProcessedLinks.incrementAndGet();

            console.println("Enqueued Links: %d -- Processed Links: %d", totalEnqueuedLinks.intValue(), totalProcessedLinks.intValue());
            console.println();

            if(isTerminated()) {
                countDownLatch.countDown();
            }
        }
    }

    @Override
    public void await() throws InterruptedException {
        log.debug("waiting...");
        countDownLatch.await();
        log.debug("process completed...");
    }

    private void enqueueValidLinks(List<URI> links) {
        links.stream()
                .filter(l -> domain.equals(getDomainName(l)) && !linkHistory.contains(l.toString()))
                .forEach(this::enqueue);
    }

    private void enqueue(URI url) {
        linkHistory.add(url.toString());
        totalEnqueuedLinks.incrementAndGet();

        executorService.submit(new WebParser(url, this));
        log.debug("Enqueued url {}", url);
    }

    private boolean isTerminated() {
        return totalEnqueuedLinks.intValue() == totalProcessedLinks.intValue();
    }
}
